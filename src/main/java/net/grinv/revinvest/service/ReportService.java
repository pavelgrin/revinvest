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

    private static final float DIVIDEND_TAX_RATE = 0.15f;

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

        logger.info("[generate] Received transaction types: {}", transactionsByType.keySet());

        Dividends dividends = this.getDividends(transactionsByType.getOrDefault(Type.Dividend, new ArrayList<>()));

        if (filter.hasTicker()) {
            TickerReport tickerReport = new TickerReport();
            tickerReport.setDividends(dividends.amount());

            report.setTickerReport(tickerReport);
        } else {
            float custodyFee = (float) transactionsByType.getOrDefault(Type.CustodyFee, new ArrayList<>()).stream()
                    .mapToDouble(Transaction::amount)
                    .sum();

            logger.debug("[generate][CommonReport] dividends: {}", dividends);
            logger.debug("[generate][CommonReport] custodyFee: {}", custodyFee);

            CommonReport commonReport = new CommonReport();
            commonReport.setDividends(dividends);
            commonReport.setCustodyFee(custodyFee);
            report.setCommonReport(commonReport);
        }

        return report;
    }

    /**
     * Calculates the total dividend amounts, including the gross amount (before tax) and the tax amount, based on a
     * list of processed dividend transactions
     *
     * <p>The method aggregates the net received amounts and then reverses the tax rate (assumed to be
     * {@code DIVIDEND_TAX_RATE}) to determine the gross amount the dividend was declared at
     */
    private Dividends getDividends(List<Transaction> dividends) {
        float amountNet =
                (float) dividends.stream().mapToDouble(Transaction::amount).sum();
        float amountWithTax = amountNet / (1 - DIVIDEND_TAX_RATE);
        logger.info("[getDividends] Dividend amount: {}", amountNet);
        return new Dividends(amountNet, amountWithTax, amountWithTax - amountNet);
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
