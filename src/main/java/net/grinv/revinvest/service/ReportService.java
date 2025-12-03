package net.grinv.revinvest.service;

import java.util.*;
import java.util.stream.Collectors;
import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.*;
import net.grinv.revinvest.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Add debug and info logs
public final class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository repo) {
        this.transactionRepository = repo;
    }

    // TODO: Add java doc
    public Report generate(Filter filter) {
        Report report = new Report();
        report.setFilter(filter);

        List<Transaction> transactions = transactionRepository.getStatement(filter);
        Map<Type, List<Transaction>> transactionsByType =
                this.normalizeData(transactions, filter).stream().collect(Collectors.groupingBy(Transaction::type));

        logger.debug("Total transactions: {}", transactionsByType.size());

        if (filter.hasTicker()) {
            report.setTickerReport(new TickerReport());
        } else {
            report.setCommonReport(new CommonReport());
        }

        return report;
    }

    // TODO: Add java doc
    // TODO: Add unit test
    List<Transaction> normalizeData(List<Transaction> transactions, Filter filter) {
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

        return transactions.stream()
                .map((t) -> {
                    float ratio = splitRatioByTicker.getOrDefault(t.ticker(), 1.0f);

                    float quantity = t.quantity() * ratio;
                    float pricePerShare = t.pricePerShare() / ratio;
                    float amount = t.amount();
                    String currency = t.currency();

                    if (filter.currency() == Currency.EUR) {
                        pricePerShare /= t.fxRate();
                        amount /= t.fxRate();
                        currency = Currency.EUR.getCode();
                    }

                    // TODO: Check fields and remove unnecessary ones
                    return new Transaction(
                            t.isoDate(),
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
