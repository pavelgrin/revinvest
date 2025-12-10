package net.grinv.revinvest.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.model.Transaction;
import net.grinv.revinvest.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TransactionRepository {
    private static final Logger logger = LoggerFactory.getLogger(TransactionRepository.class);

    private static final String SQL_INSERT_TRANSACTION =
            """
        INSERT OR REPLACE INTO Statement
        (date, timestamp, ticker, type, quantity, pricePerShare, amount, currency, fxRate)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    private static final String SQL_MIN_TIMESTAMP = "SELECT MIN(timestamp) as timestamp FROM Statement";
    private static final String SQL_MAX_TIMESTAMP = "SELECT MAX(timestamp) as timestamp FROM Statement";
    private static final String SQL_SELECT_TRANSACTIONS = "SELECT * FROM Statement";

    public TransactionRepository() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException error) {
            throw new IllegalStateException("SQLite JDBC Driver not found on classpath", error);
        }
    }

    private Connection connect() throws SQLException {
        final String dbUrl = System.getenv("DB_URL");
        return DriverManager.getConnection("jdbc:sqlite:" + dbUrl);
    }

    public List<Transaction> getStatement(Filter filter) {
        List<Transaction> transactions = new ArrayList<>();
        long fromTimestamp = DateTimeUtils.getTimestampByDate(filter.from());
        long toTimestamp = DateTimeUtils.getNextDayTimestampByDate(filter.to());

        StringBuilder sql = new StringBuilder(SQL_SELECT_TRANSACTIONS);
        sql.append(" WHERE (timestamp <= ? AND (type = '")
                .append(Type.Buy.getLabel())
                .append("' OR timestamp >= ?))");
        if (filter.hasTicker()) {
            sql.append(" AND ticker = ?");
        }
        sql.append(" ORDER BY timestamp ASC");

        logger.info("[getStatement] SQL query: {}", sql);
        try (Connection connection = this.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            preparedStatement.setLong(1, toTimestamp);
            preparedStatement.setLong(2, fromTimestamp);
            if (filter.hasTicker()) {
                preparedStatement.setString(3, filter.symbol());
            }

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    String date = rs.getString("date");
                    long timestamp = rs.getLong("timestamp");
                    String ticker = rs.getString("ticker");
                    String typeStr = rs.getString("type");
                    float quantity = rs.getFloat("quantity");
                    float pricePerShare = rs.getFloat("pricePerShare");
                    float amount = rs.getFloat("amount");
                    String currency = rs.getString("currency");
                    float fxRate = rs.getFloat("fxRate");

                    Type type = Type.getTypeByString(typeStr);

                    Transaction transaction = new Transaction(
                            date, timestamp, ticker, type, quantity, pricePerShare, amount, currency, fxRate);
                    logger.trace("[getStatement] {}", transaction);
                    transactions.add(transaction);
                }
            }
        } catch (SQLException error) {
            throw new RuntimeException("Failed to get Statement", error);
        }

        logger.info("[getStatement] Received {} records", transactions.size());
        return List.copyOf(transactions);
    }

    /**
     * Find the earliest transaction timestamp and convert it to a formatted date string
     *
     * @return string in "YYYY-MM-DD" format
     */
    public String getFirstTransactionDate() {
        try (Connection connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_MIN_TIMESTAMP);
                ResultSet rs = preparedStatement.executeQuery()) {

            if (rs.next()) {
                long timestamp = rs.getLong("timestamp");
                String date =
                        timestamp > 0 ? DateTimeUtils.getDateByTimestamp(timestamp) : DateTimeUtils.getCurrentDate();
                logger.info("[getFirstTransactionDate] Timestamp: {}; Date: {}", timestamp, date);
                return date;
            }
            throw new RuntimeException("Failed to get first transaction date");
        } catch (SQLException error) {
            throw new RuntimeException("Failed to get first transaction date", error);
        }
    }

    /**
     * Find the latest transaction timestamp and convert it to a formatted date string
     *
     * @return string in "YYYY-MM-DD" format
     */
    public String getLastTransactionDate() {
        try (Connection connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_MAX_TIMESTAMP);
                ResultSet rs = preparedStatement.executeQuery()) {

            if (rs.next()) {
                long timestamp = rs.getLong("timestamp");
                String date =
                        timestamp > 0 ? DateTimeUtils.getDateByTimestamp(timestamp) : DateTimeUtils.getCurrentDate();
                logger.info("[getLastTransactionDate] Timestamp: {}; Date: {}", timestamp, date);
                return date;
            }
            throw new RuntimeException("Failed to get last transaction date");
        } catch (SQLException error) {
            throw new RuntimeException("Failed to get last transaction date", error);
        }
    }

    /**
     * Stores {@code Transaction} objects in the database using a single batch transaction. Note that transactions with
     * the same timestamp will be overwritten
     *
     * @param transactions the list of validated Transaction objects
     */
    public void updateStatement(List<Transaction> transactions) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = this.connect();
            preparedStatement = connection.prepareStatement(SQL_INSERT_TRANSACTION);
            connection.setAutoCommit(false);

            for (Transaction t : transactions) {
                logger.trace("[updateStatement] {}", t);

                preparedStatement.setString(1, t.date());
                preparedStatement.setLong(2, t.timestamp());
                preparedStatement.setString(3, t.ticker());
                preparedStatement.setString(4, t.type().getLabel());
                preparedStatement.setFloat(5, t.quantity());
                preparedStatement.setFloat(6, t.pricePerShare());
                preparedStatement.setFloat(7, t.amount());
                preparedStatement.setString(8, t.currency());
                preparedStatement.setFloat(9, t.fxRate());

                preparedStatement.addBatch();
            }

            int[] results = preparedStatement.executeBatch();
            connection.commit();

            if (logger.isInfoEnabled()) {
                long rowsAffected = Arrays.stream(results)
                        .filter(r -> r >= 0 || r == Statement.SUCCESS_NO_INFO)
                        .count();
                logger.info("[updateStatement] Inserted {} records", rowsAffected);
            }
        } catch (SQLException error) {
            if (connection != null) {
                try {
                    logger.error("[updateStatement] Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    throw new RuntimeException("Failed to rollback transaction", rollbackError);
                }
            }

            throw new RuntimeException("Failed to update Statement", error);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
