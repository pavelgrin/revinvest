package net.grinv.revinvest.service;

import java.util.*;
import java.util.stream.Collectors;
import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.*;
import net.grinv.revinvest.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository repo) {
        this.transactionRepository = repo;
    }

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

    List<Transaction> normalizeData(List<Transaction> transactions, Filter filter) {
        Map<String, Float> quantityByTicker = new HashMap<>();
        Map<String, Float> splitRatioByTicker = new HashMap<>();

        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            Transaction transaction = iterator.next();

            switch (transaction.type()) {
                case Type.Buy:
                    quantityByTicker.merge(transaction.ticker(), transaction.quantity(), Float::sum);
                    break;
                case Type.Sell:
                    quantityByTicker.merge(transaction.ticker(), transaction.quantity() * -1, Float::sum);
                    break;
                case Type.StockSplit:
                    float quantitySum = quantityByTicker.get(transaction.ticker());
                    float quantityRatio = (transaction.quantity() + quantitySum) / quantitySum;
                    splitRatioByTicker.put(transaction.ticker(), quantityRatio);
                    iterator.remove();
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

                    return new Transaction(
                            t.isoDate(), // Do we need it at all?
                            t.date(), // Do we need it at all?
                            t.timestamp(), // Do we need it at all?
                            t.ticker(),
                            t.type(),
                            quantity,
                            pricePerShare,
                            amount,
                            currency, // Do we need it at all?
                            t.fxRate() // Do we need it at all?
                            );
                })
                .toList();
    }
}
