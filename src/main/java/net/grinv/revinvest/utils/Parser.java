package net.grinv.revinvest.utils;

import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    public static List<Transaction> parseCSVReport(InputStream inputStream) {
        List<Transaction> transactions = new ArrayList<>();

        if (inputStream == null) {
            logger.error("Input stream is null");
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine(); // Skip header line

            String line;
            while ((line = reader.readLine()) != null) {
                Transaction t = parseTransaction(line);
                if (t == null) {
                    throw new RuntimeException();
                }
                transactions.add(t);
            }
        } catch (Exception error) {
            logger.error("Error while parsing CSV report", error);
            return new ArrayList<>();
        }

        return transactions;
    }

    /**
     * Attempts to parse a single line of CSV into Transaction.
     * Returns null if parsing fails and logs error.
     * <p>
     * Expecting line format (some fields may be empty):<br>
     * {@code Date,Ticker,Type,Quantity,Price per share,Total Amount,Currency,FX Rate}
     * <p>
     * Date is in ISO format: {@code 1970-01-01T00:00:00.000Z}<br>
     * FX Rate is EUR/USD currency
     */
    private static Transaction parseTransaction(String line) {
        String[] fields = line.split(",");

        if (fields.length < 8) {
            logger.error("Skipped line due to insufficient number of fields");
            return null;
        }
        try {
            String isoDate = fields[0].trim();
            String data = DateTimeUtils.getDateTime(isoDate);
            long timestamp = DateTimeUtils.getTimestamp(isoDate);

            String ticker = fields[1].isBlank() ? null : fields[1].trim();
            Type type = Type.getTypeByString(fields[2].trim());

            float quantity = fields[3].isBlank() ? 0 :  Float.parseFloat(fields[3]);
            float pricePerShare = fields[4].isBlank() ? 0 : Float.parseFloat(fields[4]);
            float amount = Float.parseFloat(fields[5]);

            Currency currency = Currency.getCurrencyByString(fields[6].trim());

            float fxRate = Float.parseFloat(fields[7]);

            return new Transaction(isoDate, data, timestamp, ticker, type, quantity, pricePerShare, amount, currency, fxRate);
        } catch (Exception error) {
            logger.error("Skipped line due to invalid data", error);
            return null;
        }
    }
}
