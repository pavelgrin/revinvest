package net.grinv.revinvest.migration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// TODO: Cover this class with comments
public final class MigrationService
{
    // TODO: Move to env
    private final static String SQL_MIGRATION_DIR = "/migrations/";
    private final static String DB_URL = "jdbc:sqlite:src/main/resources/transactions.db";
    
    private final List<Map.Entry<String, String>> migrationFiles = List.of(Map.entry(
        "v1_create_transactions_table.sql", "u1_create_transactions_table.sql"));
    
    private MigrationService()
    {
    }
    
    public static void main(String[] args)
    {
        String command = args.length == 0 ? "up" : args[0];
        MigrationService service = new MigrationService();
        
        try
        {
            switch (command)
            {
                case "up":
                    service.up();
                    break;
                case "down":
                    service.down();
                    break;
                default:
                    // TODO: Add and use logger service
                    System.err.println("Unknown command: " + command);
            }
        }
        catch (SQLException e)
        {
            // TODO: Add and use logger service
            System.err.println(e.getMessage());
        }
    }
    
    private void up() throws SQLException
    {
        try (Connection connection = DriverManager.getConnection(DB_URL);)
        {
            // TODO: Add and use logger service
            System.out.println("DB connected at " + DB_URL);
            
            createSchemaVersionTable(connection);
            
            String lastMigration = getLastAppliedMigration(connection);
            int lastMigrationIndex = this.findIndexOfKey(this.migrationFiles, lastMigration);
            for (int i = lastMigrationIndex + 1; i < this.migrationFiles.size(); ++i)
            {
                Map.Entry<String, String> item = this.migrationFiles.get(i);
                String migration = item.getKey();
                
                String sql = readSqlScript(migration);
                try (Statement statement = connection.createStatement())
                {
                    statement.execute(sql);
                }
                recordAppliedMigration(connection, migration);
            }
            
            // TODO: Add and use logger service
            System.out.println("Migrations applied successfully");
        }
    }
    
    private void down() throws SQLException
    {
        try (Connection connection = DriverManager.getConnection(DB_URL);)
        {
            // TODO: Add and use logger service
            System.out.println("DB connected at " + DB_URL);
            
            createSchemaVersionTable(connection);
            
            String lastMigration = getLastAppliedMigration(connection);
            int lastMigrationIndex = this.findIndexOfKey(this.migrationFiles, lastMigration);
            
            if (lastMigrationIndex == -1)
            {
                // TODO: Add and use logger service
                System.out.println("No migrations to rollback");
                return;
            }
            
            for (int i = lastMigrationIndex; i >= 0; --i)
            {
                Map.Entry<String, String> item = this.migrationFiles.get(i);
                String migration = item.getKey();
                String rollback = item.getValue();
                
                String sql = readSqlScript(rollback);
                try (Statement statement = connection.createStatement())
                {
                    statement.execute(sql);
                }
                deleteAppliedMigrationRecord(connection, migration);
            }
            
            // TODO: Add and use logger service
            System.out.println("Migrations reverted successfully");
        }
    }
    
    private void createSchemaVersionTable(Connection connection) throws SQLException
    {
        String sql = """
            create table if not exists SchemaVersion
            (
                version varchar(255) primary key,
                created integer default current_timestamp
            )
            """;
        try (Statement statement = connection.createStatement())
        {
            statement.execute(sql);
        }
    }
    
    private void recordAppliedMigration(Connection connection, String version) throws SQLException
    {
        String sql = "insert into SchemaVersion (version) values (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setString(1, version);
            preparedStatement.executeUpdate();
        }
    }
    
    private String getLastAppliedMigration(Connection connection) throws SQLException
    {
        String sql = "select version from SchemaVersion order by created desc LIMIT 1";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql))
        {
            if (rs.next())
            {
                return rs.getString("version");
            }
        }
        return null;
    }
    
    private void deleteAppliedMigrationRecord(Connection connection, String version) throws SQLException
    {
        String sql = "delete from SchemaVersion where version = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setString(1, version);
            preparedStatement.executeUpdate();
        }
    }
    
    private String readSqlScript(String fileName)
    {
        try (InputStream is = this.getClass().getResourceAsStream(SQL_MIGRATION_DIR + fileName))
        {
            assert is != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is)))
            {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
        catch (Exception e)
        {
            // TODO: Add and use logger service
            throw new IllegalStateException("Failed to read SQL migration script: " + fileName, e);
        }
    }
    
    private int findIndexOfKey(List<Map.Entry<String, String>> list, String key)
    {
        if (key == null)
        {
            return -1;
        }
        for (int i = 0; i < list.size(); ++i)
        {
            Map.Entry<String, String> item = list.get(i);
            if (item.getKey().equals(key))
            {
                return i;
            }
        }
        return -1;
    }
}
