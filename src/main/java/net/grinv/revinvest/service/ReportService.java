package net.grinv.revinvest.service;

import java.util.*;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.*;
import net.grinv.revinvest.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final TransactionRepository transactionRepository;
    private final SummaryCalculator totalsCalculator;
    private final TransactionNormalizer transactionNormalizer;

    public ReportService(TransactionRepository repo, SummaryCalculator calculator, TransactionNormalizer normalizer) {
        this.transactionRepository = repo;
        this.totalsCalculator = calculator;
        this.transactionNormalizer = normalizer;
    }

    public Report generate(Filter filter) {
        Report report = new Report();
        report.setFilter(filter);

        List<Transaction> transactions = transactionRepository.getStatement(filter);
        Map<Type, List<Transaction>> transactionsByType = this.transactionNormalizer.prepareData(transactions, filter);
        logger.info("[generate] Received transaction types: {}", transactionsByType.keySet());

        if (filter.hasTicker()) {
            report.setTickerReport(this.getTickerReport(transactionsByType));
        } else {
            report.setCommonReport(this.getCommonReport(transactionsByType));
        }
        return report;
    }

    private TickerReport getTickerReport(Map<Type, List<Transaction>> transactionsByType) {
        List<Transaction> dividendTransactions = transactionsByType.getOrDefault(Type.Dividend, new ArrayList<>());
        List<Transaction> buyTransactions = transactionsByType.getOrDefault(Type.Buy, new ArrayList<>());
        List<Transaction> sellTransactions = transactionsByType.getOrDefault(Type.Sell, new ArrayList<>());

        Dividends dividends = this.totalsCalculator.getDividends(dividendTransactions);
        logger.debug("[getTickerReport] {}", dividends);

        List<SellSummary> summaryLIFO =
                this.totalsCalculator.getSellSummary(buyTransactions.reversed(), sellTransactions);
        float totalLIFO = this.totalsCalculator.getSellSummaryTotal(summaryLIFO);
        logger.debug("[getTickerReport] totalLIFO: {}", totalLIFO);

        TickerSummary summary = this.totalsCalculator.getTickerSummary(buyTransactions, sellTransactions);
        logger.debug("[getTickerReport] summary: {}", summary);

        return new TickerReport(dividends.amount(), summary, totalLIFO, summaryLIFO);
    }

    private CommonReport getCommonReport(Map<Type, List<Transaction>> transactionsByType) {
        List<Transaction> dividendTransactions = transactionsByType.getOrDefault(Type.Dividend, new ArrayList<>());
        List<Transaction> buyTransactions = transactionsByType.getOrDefault(Type.Buy, new ArrayList<>());
        List<Transaction> sellTransactions = transactionsByType.getOrDefault(Type.Sell, new ArrayList<>());
        List<Transaction> topUpTransactions = transactionsByType.getOrDefault(Type.TopUp, new ArrayList<>());
        List<Transaction> withdrawTransactions = transactionsByType.getOrDefault(Type.Withdraw, new ArrayList<>());

        Dividends dividends = this.totalsCalculator.getDividends(dividendTransactions);
        logger.debug("[getCommonReport] {}", dividends);

        List<SellSummary> summaryFIFO = this.totalsCalculator.getSellSummary(buyTransactions, sellTransactions);
        List<SellSummary> summaryLIFO =
                this.totalsCalculator.getSellSummary(buyTransactions.reversed(), sellTransactions);
        float totalFIFO = this.totalsCalculator.getSellSummaryTotal(summaryFIFO);
        float totalLIFO = this.totalsCalculator.getSellSummaryTotal(summaryLIFO);
        logger.debug("[getCommonReport] totalFIFO: {}", totalFIFO);
        logger.debug("[getCommonReport] totalLIFO: {}", totalLIFO);

        float depositAmount = this.totalsCalculator.getTotalAmount(topUpTransactions);
        float withdrawalAmount = this.totalsCalculator.getTotalAmount(withdrawTransactions);
        float balance = depositAmount + withdrawalAmount;
        float custodyFee = this.totalsCalculator.getTotalAmount(
                transactionsByType.getOrDefault(Type.CustodyFee, new ArrayList<>()));

        logger.debug("[getCommonReport] depositAmount: {}", depositAmount);
        logger.debug("[getCommonReport] withdrawalAmount: {}", withdrawalAmount);
        logger.debug("[getCommonReport] custodyFee: {}", custodyFee);

        return new CommonReport(balance, dividends, custodyFee, totalFIFO, totalLIFO, summaryFIFO, summaryLIFO);
    }
}
