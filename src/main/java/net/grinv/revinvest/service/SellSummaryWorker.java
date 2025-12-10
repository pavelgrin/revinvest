package net.grinv.revinvest.service;

import java.util.Deque;
import java.util.LinkedList;
import net.grinv.revinvest.model.SellSummary;
import net.grinv.revinvest.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SellSummaryWorker {
    private static final Logger logger = LoggerFactory.getLogger(SellSummaryWorker.class);
    private final Deque<Transaction> inventory;

    public SellSummaryWorker(Deque<Transaction> inventory) {
        this.inventory = inventory;
    }

    /** Calculates the realized capital gain for a single sale and consumes the necessary lots from buyTransactions */
    public SellSummary getNext(Transaction sell) {
        logger.trace("[getNext] Sell: {}", sell);

        // Collect info about transactions that were skipped or partially processed to return them to the queue later
        Deque<Transaction> skippedLots = new LinkedList<>();

        float remainingQuantitySold = sell.quantity();
        float costBasis = 0.0f;

        while (!this.inventory.isEmpty() && remainingQuantitySold > 0.0f) {
            // TODO: Need to check how effective it is to poll items and return them back to the queue. Compare with
            // other options
            Transaction buy = this.inventory.poll();
            logger.trace("[getNext] Buy: {}", buy);

            if (!buy.ticker().equals(sell.ticker()) || buy.timestamp() >= sell.timestamp()) {
                skippedLots.offer(buy);
                continue;
            }

            logger.trace("[getNext] remainingQuantitySold: {}", remainingQuantitySold);

            float quantityApplied = Math.min(buy.quantity(), remainingQuantitySold);
            remainingQuantitySold -= quantityApplied;
            costBasis += quantityApplied * buy.pricePerShare();

            if (buy.quantity() > quantityApplied) {
                float remainingQuantityBought = buy.quantity() - quantityApplied;
                float remainingAmount = remainingQuantityBought * buy.pricePerShare();
                skippedLots.offer(new Transaction(
                        buy.date(),
                        buy.timestamp(),
                        buy.ticker(),
                        buy.type(),
                        remainingQuantityBought,
                        buy.pricePerShare(),
                        remainingAmount,
                        buy.currency(),
                        buy.fxRate()));
            }
        }

        while (!skippedLots.isEmpty()) {
            this.inventory.offerFirst(skippedLots.pollLast());
        }

        return new SellSummary(
                sell.date(), sell.ticker(), sell.quantity(), costBasis, sell.amount(), sell.amount() - costBasis);
    }
}
