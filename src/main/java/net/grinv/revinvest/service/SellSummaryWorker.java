package net.grinv.revinvest.service;

import java.util.Queue;
import net.grinv.revinvest.model.SellSummary;
import net.grinv.revinvest.model.Transaction;

public class SellSummaryWorker {
    private final Queue<Transaction> buyTransactions;

    public SellSummaryWorker(Queue<Transaction> buyTransactions) {
        this.buyTransactions = buyTransactions;
    }

    /** Calculates the realized capital gain for a single sale and consumes the necessary lots from buyTransactions */
    public SellSummary getNext(Transaction sellTransaction) {
        return new SellSummary(sellTransaction.date(), sellTransaction.ticker(), 0.0f, 0.0f, 0.0f, 0.0f);
    }
}
