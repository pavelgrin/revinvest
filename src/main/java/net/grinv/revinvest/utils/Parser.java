package net.grinv.revinvest.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.Transaction;

public final class Parser {
    public static List<Transaction> parseCSVReport(InputStream inputStream) {
        List<Transaction> transactions = new ArrayList<>();

        if (inputStream == null) {
            throw new RuntimeException("Input stream is null");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine(); // Skip header line

            String line;
            while ((line = reader.readLine()) != null) {
                Transaction t = parseTransaction(line);
                transactions.add(t);
            }
        } catch (Exception error) {
            throw new RuntimeException("Error while parsing CSV report", error);
        }

        return transactions;
    }

    /**
     * Attempts to parse a single line of CSV into Transaction
     *
     * <p>Expecting line format (some fields may be empty):<br>
     * {@code Date,Ticker,Type,Quantity,Price per share,Total Amount,Currency,FX Rate}
     *
     * <p>Date is in ISO format: {@code 1970-01-01T00:00:00.000Z}<br>
     * FX Rate is EUR/USD currency
     */
    private static Transaction parseTransaction(String line) {
        String[] fields = line.split(",");

        if (fields.length < 8) {
            throw new RuntimeException("Skipped line due to insufficient number of fields");
        }
        try {
            String isoDate = fields[0].trim();
            String data = DateTimeUtils.getDateTime(isoDate);
            long timestamp = DateTimeUtils.getTimestamp(isoDate);

            String ticker = fields[1].isBlank() ? null : fields[1].trim();
            Type type = Type.getTypeByString(fields[2].trim());

            float quantity = parseFloat(fields[3]);
            float pricePerShare = parseFloat(fields[4]);
            float amount = parseFloat(fields[5]);

            Currency currency = Currency.getCurrencyByString(fields[6].trim());

            float fxRate = parseFloat(fields[7]);

            return new Transaction(
                    isoDate, data, timestamp, ticker, type, quantity, pricePerShare, amount, currency, fxRate);

        } catch (Exception error) {
            throw new RuntimeException("Skipped line due to invalid data", error);
        }
    }

    private static float parseFloat(String value) {
        String numericString = value.replaceAll("[^\\d.-]", "");
        if (numericString.isBlank()) {
            return 0.0f;
        }
        return Float.parseFloat(numericString);
    }
}
