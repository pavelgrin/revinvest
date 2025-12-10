package net.grinv.revinvest.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionNormalizer {
    private static final Logger logger = LoggerFactory.getLogger(TransactionNormalizer.class);

    public Map<Type, List<Transaction>> prepareData(List<Transaction> transactions, Filter filter) {
        return this.normalizeData(transactions, filter).stream().collect(Collectors.groupingBy(Transaction::type));
    }

    /**
     * Executes the two main data normalization steps:
     *
     * <ol>
     *   <li>Applies stock split to all affected transactions
     *   <li>Conditionally converts amounts into the base currency (EUR)
     * </ol>
     */
    private List<Transaction> normalizeData(List<Transaction> transactions, Filter filter) {
        Map<String, Float> quantityByTicker = new HashMap<>();
        Map<String, Float> splitRatioByTicker = new HashMap<>();

        for (Transaction t : transactions) {
            switch (t.type()) {
                case Type.Buy:
                    quantityByTicker.merge(t.ticker(), t.quantity(), Float::sum);
                    break;
                case Type.Sell:
                    quantityByTicker.merge(t.ticker(), t.quantity() * -1, Float::sum);
                    break;
                case Type.StockSplit:
                    float quantitySum = quantityByTicker.get(t.ticker());
                    float quantityRatio = (t.quantity() + quantitySum) / quantitySum;
                    splitRatioByTicker.put(t.ticker(), quantityRatio);
                    break;
            }
        }

        logger.info("[normalizeData] Split coefficient: {}", splitRatioByTicker);
        return transactions.stream()
                .map((t) -> {
                    float ratio = splitRatioByTicker.getOrDefault(t.ticker(), 1.0f);

                    float quantity = t.quantity() * ratio;
                    float pricePerShare = t.pricePerShare() / ratio;
                    float amount = t.amount();
                    String currency = t.currency();

                    if (filter.currency() == Currency.EUR) {
                        pricePerShare *= t.fxRate();
                        amount *= t.fxRate();
                        currency = Currency.EUR.getCode();
                    }

                    return new Transaction(
                            t.date(),
                            t.timestamp(),
                            t.ticker(),
                            t.type(),
                            quantity,
                            pricePerShare,
                            amount,
                            currency,
                            t.fxRate());
                })
                .toList();
    }
}
