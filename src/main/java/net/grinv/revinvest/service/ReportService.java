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
        logger.debug("[generate] {}", dividends);

        List<Transaction> buyTransactions = transactionsByType.getOrDefault(Type.Buy, new ArrayList<>());
        List<Transaction> sellTransactions = transactionsByType.getOrDefault(Type.Sell, new ArrayList<>());

        SellSummaryWorker sellWorkerFIFO = new SellSummaryWorker(new LinkedList<>(buyTransactions));
        SellSummaryWorker sellWorkerLIFO = new SellSummaryWorker(new LinkedList<>(buyTransactions.reversed()));

        List<SellSummary> summaryFIFO =
                sellTransactions.stream().map(sellWorkerFIFO::getNext).toList();
        List<SellSummary> summaryLIFO =
                sellTransactions.stream().map(sellWorkerLIFO::getNext).toList();

        float totalFIFO =
                (float) summaryFIFO.stream().mapToDouble(SellSummary::pnl).sum();
        float totalLIFO =
                (float) summaryLIFO.stream().mapToDouble(SellSummary::pnl).sum();
        logger.debug("[generate] totalFIFO: {}", totalFIFO);
        logger.debug("[generate] totalLIFO: {}", totalLIFO);

        if (filter.hasTicker()) {
            float buyAmount = this.getTotalAmount(buyTransactions);
            float sellAmount = this.getTotalAmount(sellTransactions);
            float buyQuantity = this.getTotalQuantity(buyTransactions);
            float sellQuantity = this.getTotalQuantity(sellTransactions);
            float netAmount = buyAmount - sellAmount;
            float quantity = netAmount > 0.0f ? buyQuantity - sellQuantity : 0.0f;
            float bep = netAmount > 0.0f ? netAmount / quantity : 0.0f;

            TickerSummary summary = new TickerSummary(buyAmount, sellAmount, netAmount, quantity, bep);
            logger.debug("[generate][TickerReport] summary: {}", summary);

            TickerReport tickerReport = new TickerReport();

            tickerReport.setDividends(dividends.amount());
            tickerReport.setSummary(summary);
            tickerReport.setPnlTotal(totalLIFO);
            tickerReport.setSellsSummary(summaryLIFO);

            report.setTickerReport(tickerReport);
        } else {
            float depositAmount = this.getTotalAmount(transactionsByType.getOrDefault(Type.TopUp, new ArrayList<>()));
            float withdrawalAmount =
                    this.getTotalAmount(transactionsByType.getOrDefault(Type.Withdraw, new ArrayList<>()));
            float custodyFee = this.getTotalAmount(transactionsByType.getOrDefault(Type.CustodyFee, new ArrayList<>()));

            logger.debug("[generate][CommonReport] depositAmount: {}", depositAmount);
            logger.debug("[generate][CommonReport] withdrawalAmount: {}", withdrawalAmount);
            logger.debug("[generate][CommonReport] custodyFee: {}", custodyFee);

            CommonReport commonReport = new CommonReport();

            commonReport.setBalance(depositAmount + withdrawalAmount);
            commonReport.setDividends(dividends);
            commonReport.setCustodyFee(custodyFee);
            commonReport.setCustodyFee(custodyFee);
            commonReport.setTotalFIFO(totalFIFO);
            commonReport.setTotalLIFO(totalLIFO);
            commonReport.setSummaryFIFO(summaryFIFO);
            commonReport.setSummaryLIFO(summaryLIFO);

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
        float amountNet = this.getTotalAmount(dividends);
        float amountWithTax = amountNet / (1 - DIVIDEND_TAX_RATE);
        return new Dividends(amountNet, amountWithTax, amountWithTax - amountNet);
    }

    private float getTotalAmount(List<Transaction> transactions) {
        return (float) transactions.stream().mapToDouble(Transaction::amount).sum();
    }

    private float getTotalQuantity(List<Transaction> transactions) {
        return (float) transactions.stream().mapToDouble(Transaction::quantity).sum();
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
