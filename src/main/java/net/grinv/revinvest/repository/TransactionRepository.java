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
        String dbUrl = System.getenv("DB_URL");
        return DriverManager.getConnection(dbUrl);
    }

    public void updateStatement(InputStream inputStream) {
        //
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
}
