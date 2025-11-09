package net.grinv.revinvest.service;

import java.util.List;
import net.grinv.revinvest.model.*;
import net.grinv.revinvest.repository.TransactionRepository;
import net.grinv.revinvest.utils.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository repo) {
        this.transactionRepository = repo;
    }

    public Report generate(Filter rawFilter) {
        Filter filter = this.normalizeFilter(rawFilter);
        Report report = new Report();

        report.setFilter(filter);

        List<Transaction> transactions = transactionRepository.getAllTransactions();
        logger.debug("Total transactions: {}", transactions.size());

        if (filter.hasTicker()) {
            report.setTickerReport(new TickerReport());
        } else {
            report.setCommonReport(new CommonReport());
        }

        return report;
    }

    private Filter normalizeFilter(Filter filter) {
        String from = filter.from() == null || filter.from().isEmpty() ? this.getFirstTransactionDate() : filter.from();
        String to = filter.to() == null || filter.to().isEmpty() ? this.getLatestTransactionDate() : filter.to();
        return new Filter(from, to, filter.symbol(), filter.currency());
    }

    private String getFirstTransactionDate() {
        return DateTime.getDate("");
    }

    private String getLatestTransactionDate() {
        return DateTime.getDate("");
    }
}
