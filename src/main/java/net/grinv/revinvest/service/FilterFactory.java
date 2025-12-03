package net.grinv.revinvest.service;

import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.repository.TransactionRepository;

public class FilterFactory {
    private final TransactionRepository transactionRepository;

    public FilterFactory(TransactionRepository repo) {
        this.transactionRepository = repo;
    }

    // TODO: Add java doc
    // TODO: Add unit test
    public Filter build(String fromParam, String toParam, String symbolParam, String currencyParam) {
        String from = fromParam == null || fromParam.isBlank()
                ? this.transactionRepository.getFirstTransactionDate()
                : fromParam;

        String to =
                toParam == null || toParam.isBlank() ? this.transactionRepository.getLastTransactionDate() : toParam;

        String symbol = symbolParam == null || symbolParam.isBlank() ? null : symbolParam;

        Currency currency = currencyParam == null || currencyParam.isBlank()
                ? Currency.ORIGINAL
                : Currency.getCurrencyByString(currencyParam);

        return new Filter(from, to, symbol, currency);
    }
}
