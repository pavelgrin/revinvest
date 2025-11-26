package net.grinv.revinvest.service;

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

        // List<Transaction> transactions = transactionRepository.getStatement(filter);
        // logger.debug("Total transactions: {}", transactions.size());

        if (filter.hasTicker()) {
            report.setTickerReport(new TickerReport());
        } else {
            report.setCommonReport(new CommonReport());
        }

        return report;
    }
}
