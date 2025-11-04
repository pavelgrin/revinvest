package net.grinv.revinvest.repository;

import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public final class TransactionRepository
{
    private Connection connect() throws SQLException
    {
        String dbUrl = System.getenv("DB_URL");
        return DriverManager.getConnection(dbUrl);
    }

    public List<Transaction> getAllTransactions()
    {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Statement";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery())
        {
            while (rs.next())
            {
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
                    isoDate,
                    date,
                    timestamp,
                    ticker,
                    type,
                    quantity,
                    pricePerShare,
                    amount,
                    currency,
                    fxRate
                ));
            }
        }
        catch (SQLException e)
        {
            // TODO: Add and use logger service
            System.err.println(e.getMessage());
        }
        return transactions;
    }
}
