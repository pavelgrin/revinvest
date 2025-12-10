package net.grinv.revinvest.service;

import java.util.LinkedList;
import java.util.List;
import net.grinv.revinvest.model.Dividends;
import net.grinv.revinvest.model.SellSummary;
import net.grinv.revinvest.model.TickerSummary;
import net.grinv.revinvest.model.Transaction;

public class SummaryCalculator {
    private static final float DIVIDEND_TAX_RATE = 0.15f;

    /**
     * Calculates the total dividend amounts, including the gross amount (before tax) and the tax amount, based on a
     * list of processed dividend transactions
     *
     * <p>The method aggregates the net received amounts and then reverses the tax rate (assumed to be
     * {@code DIVIDEND_TAX_RATE}) to determine the gross amount the dividend was declared at
     */
    public Dividends getDividends(List<Transaction> dividends) {
        float amountNet = this.getTotalAmount(dividends);
        float amountWithTax = amountNet / (1 - DIVIDEND_TAX_RATE);
        return new Dividends(amountNet, amountWithTax, amountWithTax - amountNet);
    }

    public TickerSummary getTickerSummary(List<Transaction> buyTransactions, List<Transaction> sellTransactions) {
        float buyAmount = this.getTotalAmount(buyTransactions);
        float sellAmount = this.getTotalAmount(sellTransactions);
        float buyQuantity = this.getTotalQuantity(buyTransactions);
        float sellQuantity = this.getTotalQuantity(sellTransactions);

        float netAmount = buyAmount - sellAmount;
        float quantity = netAmount > 0.0f ? buyQuantity - sellQuantity : 0.0f;
        float bep = netAmount > 0.0f ? netAmount / quantity : 0.0f;

        return new TickerSummary(buyAmount, sellAmount, netAmount, quantity, bep);
    }

    public List<SellSummary> getSellSummary(List<Transaction> buyTransactions, List<Transaction> sellTransactions) {
        SellSummaryWorker sellWorker = new SellSummaryWorker(new LinkedList<>(buyTransactions));
        return sellTransactions.stream().map(sellWorker::getNext).toList();
    }

    public float getSellSummaryTotal(List<SellSummary> summary) {
        return (float) summary.stream().mapToDouble(SellSummary::pnl).sum();
    }

    public float getTotalAmount(List<Transaction> transactions) {
        return (float) transactions.stream().mapToDouble(Transaction::amount).sum();
    }

    public float getTotalQuantity(List<Transaction> transactions) {
        return (float) transactions.stream().mapToDouble(Transaction::quantity).sum();
    }
}
