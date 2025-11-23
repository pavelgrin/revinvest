package net.grinv.revinvest.repository;

import java.io.InputStream;
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

    private Connection connect() throws SQLException {
        final String dbUrl = System.getenv("DB_URL");
        return DriverManager.getConnection(dbUrl);
    }

    public List<Transaction> getStatement(Filter filter) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Statement";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                String isoDate = rs.getString("isoDate");
                String date = rs.getString("date");
                int timestamp = rs.getInt("timestamp");
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
            logger.error("", error);
        }
        return transactions;
    }

    public void updateStatement(List<Transaction> transactions) {
        //
    }

    // public int saveTransactionsFromCsv(InputStream inputStream) {
    //     int rowsSaved = 0;
    //
    //     try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
    //         // Skip the header row (if present)
    //         reader.readLine();
    //
    //         String line;
    //         List<Transaction> transactionsToSave = new ArrayList<>();
    //
    //         while ((line = reader.readLine()) != null) {
    //             // Example CSV format: timestamp,symbol,amount,currency
    //             String[] fields = line.split(",");
    //
    //             if (fields.length < 4) continue; // Skip malformed rows
    //
    //             try {
    //                 // Map raw strings to a Transaction object (or its required components)
    //                 // You'll use your DateTimeUtils and CurrencyUtils here
    //                 // This assumes your Transaction constructor takes these types
    //                 Transaction t = createTransactionFromFields(fields);
    //                 transactionsToSave.add(t);
    //
    //             } catch (Exception e) {
    //                 System.err.println("Skipping malformed row: " + line);
    //             }
    //         }
    //
    //         // Execute batch insertion
    //         rowsSaved = insertTransactionsBatch(transactionsToSave);
    //
    //     } catch (IOException e) {
    //         System.err.println("Error reading CSV stream.");
    //     }
    //     return rowsSaved;
    // }
    //
    // // Placeholder for data mapping (use your DateTimeUtils, CurrencyUtils)
    // private Transaction createTransactionFromFields(String[] fields) {
    //     // Add your logic to convert fields[0] to Instant, fields[3] to Currency, etc.
    //     // Example: return new Transaction(..., fields[1], Double.parseDouble(fields[2]), ...);
    //     return null; // Replace with actual object creation
    // }
    //
    // private int insertTransactionsBatch(List<Transaction> transactions) {
    //     String sql = "INSERT INTO transactions (timestamp, symbol, amount, currency) VALUES (?, ?, ?, ?)";
    //     int[] results = null;
    //
    //     try (Connection conn = connect();
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {
    //
    //         // Set auto commit to false for a single transaction
    //         conn.setAutoCommit(false);
    //
    //         for (Transaction t : transactions) {
    //             // Map the Transaction object fields to the prepared statement indices
    //             // Assuming timestamp is stored as a long (milliseconds)
    //             pstmt.setLong(1, t.timestamp().toEpochMilli());
    //             pstmt.setString(2, t.symbol());
    //             pstmt.setDouble(3, t.amount());
    //             pstmt.setString(4, t.currency().toString());
    //
    //             // Add the statement to the batch
    //             pstmt.addBatch();
    //         }
    //
    //         // Execute all batched statements
    //         results = pstmt.executeBatch();
    //
    //         // Commit the transaction if all batches succeeded
    //         conn.commit();
    //
    //     } catch (SQLException e) {
    //         System.err.println("Batch insertion failed: " + e.getMessage());
    //         // Handle rollback if necessary
    //         // ...
    //     }
    //
    //     // Sum rows affected, typically results.length
    //     return results != null ? results.length : 0;
    // }
    // }

}
