package net.grinv.revinvest.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TransactionRepository {
    private static final Logger logger = LoggerFactory.getLogger(TransactionRepository.class);

    private static final String SQL_INSERT_TRANSACTION =
            """
        INSERT OR REPLACE INTO Statement
        (isoDate, date, timestamp, ticker, type, quantity, pricePerShare, amount, currency, fxRate)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

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
        String sql = "SELECT * FROM Statement";

        try (Connection connection = this.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                String isoDate = rs.getString("isoDate");
                String date = rs.getString("date");
                long timestamp = rs.getLong("timestamp");
                String ticker = rs.getString("ticker");
                String typeStr = rs.getString("type");
                float quantity = rs.getFloat("quantity");
                float pricePerShare = rs.getFloat("pricePerShare");
                float amount = rs.getFloat("amount");
                String currencyStr = rs.getString("currency");
                float fxRate = rs.getFloat("fxRate");

                Currency currency = Currency.getCurrencyByString(currencyStr);
                Type type = Type.getTypeByString(typeStr);
                transactions.add(new Transaction(
                        isoDate, date, timestamp, ticker, type, quantity, pricePerShare, amount, currency, fxRate));
            }
        } catch (SQLException error) {
            throw new RuntimeException("Failed to get Statement", error);
        }
        return transactions;
    }

    /**
     * Saves {@code Transaction} objects to the database using a single batch transaction
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
                preparedStatement.setString(1, t.isoDate());
                preparedStatement.setString(2, t.date());
                preparedStatement.setLong(3, t.timestamp());
                preparedStatement.setString(4, t.ticker());
                preparedStatement.setString(5, t.type().getLabel());
                preparedStatement.setFloat(6, t.quantity());
                preparedStatement.setFloat(7, t.pricePerShare());
                preparedStatement.setFloat(8, t.amount());
                preparedStatement.setString(9, t.currency().getCode());
                preparedStatement.setFloat(10, t.fxRate());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException error) {
            if (connection != null) {
                try {
                    logger.error("Transaction is being rolled back");
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
