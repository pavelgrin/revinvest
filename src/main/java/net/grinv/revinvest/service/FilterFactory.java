package net.grinv.revinvest.service;

import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterFactory {
    private static final Logger logger = LoggerFactory.getLogger(FilterFactory.class);

    private final TransactionRepository transactionRepository;

    public FilterFactory(TransactionRepository repo) {
        this.transactionRepository = repo;
    }

    /**
     * Builds a complete, validated {@link Filter} object from raw request parameters. This method handles defaulting
     * the date range by querying the database for the earliest and latest transaction timestamps if the parameters are
     * missing
     */
    public Filter build(String fromParam, String toParam, String symbolParam, String currencyParam) {
        logger.debug(
                "[build] Params: from={}, to={}, symbol={}, currency={}",
                fromParam,
                toParam,
                symbolParam,
                currencyParam);

        String from = fromParam == null || fromParam.isBlank()
                ? this.transactionRepository.getFirstTransactionDate()
                : fromParam;

        String to =
                toParam == null || toParam.isBlank() ? this.transactionRepository.getLastTransactionDate() : toParam;

        String symbol = symbolParam == null || symbolParam.isBlank() ? null : symbolParam;

        Currency currency = currencyParam == null || currencyParam.isBlank()
                ? Currency.ORIGINAL
                : Currency.getCurrencyByString(currencyParam);

        logger.info("[build] Filter parameters: from={}, to={}, symbol={}, currency={}", from, to, symbol, currency);
        return new Filter(from, to, symbol, currency);
    }
}
