package net.grinv.revinvest.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.Transaction;
import net.grinv.revinvest.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    public List<Transaction> parseCSVReport(InputStream inputStream) {
        List<Transaction> transactions = new ArrayList<>();

        if (inputStream == null) {
            throw new RuntimeException("Input stream is null");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine(); // Skip header line

            String line;
            while ((line = reader.readLine()) != null) {
                Transaction t = this.parseTransaction(line);
                transactions.add(t);
            }
        } catch (Exception error) {
            throw new RuntimeException("Error while parsing CSV report", error);
        }

        logger.info("[parseCSVReport] Parsed {} lines from csv file", transactions.size());
        return List.copyOf(transactions);
    }

    /**
     * Attempts to parse a single line of CSV into Transaction
     *
     * <p>Expecting line format (some fields may be empty):<br>
     * {@code Date,Ticker,Type,Quantity,Price per share,Total Amount,Currency,FX Rate}
     *
     * <p>Date is in ISO format: {@code 1970-01-01T00:00:00.000[000]Z}<br>
     * FX Rate is the exchange rate, for example USD to EUR
     */
    private Transaction parseTransaction(String line) {
        String[] fields = line.split(",");

        if (fields.length < 8) {
            throw new RuntimeException("Skipped line due to insufficient number of fields");
        }
        try {
            String isoDate = fields[0].trim();
            String date = DateTimeUtils.getDateTime(isoDate);
            long timestamp = DateTimeUtils.getTimestamp(isoDate);

            String ticker = fields[1].isBlank() ? null : fields[1].trim();
            Type type = Type.getTypeByString(fields[2].trim());

            float quantity = this.parseFloat(fields[3]);
            float pricePerShare = this.parseFloat(fields[4]);
            float amount = this.parseFloat(fields[5]);

            String currency = fields[6].trim();

            float fxRate = this.parseFloat(fields[7]);

            logger.trace("[parseTransaction] {}", line);
            return new Transaction(date, timestamp, ticker, type, quantity, pricePerShare, amount, currency, fxRate);

        } catch (Exception error) {
            throw new RuntimeException("Skipped line due to invalid data", error);
        }
    }

    /**
     * Safely parses a string into a float value after removing all non-numeric characters
     *
     * <p>This method is resistant to currency symbols (e.g. "USD 99.99") and extraneous spaces
     */
    private float parseFloat(String value) {
        String numericString = value.replaceAll("[^\\d.-]", "");
        if (numericString.isBlank()) {
            return 0.0f;
        }
        return Float.parseFloat(numericString);
    }
}
