package net.grinv.revinvest.service;

import java.util.LinkedList;
import java.util.Queue;
import net.grinv.revinvest.consts.Type;
import net.grinv.revinvest.model.SellSummary;
import net.grinv.revinvest.model.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SellSummaryWorkerTest {
    private Queue<Transaction> inventory;

    @BeforeEach
    void setUp() {
        Transaction buyTransaction1 = new Transaction("", 1L, "", Type.Buy, 10.0f, 1.0f, 10.0f, "", 1.0f);
        Transaction buyTransaction2 = new Transaction("", 2L, "", Type.Buy, 10.0f, 2.0f, 20.0f, "", 1.0f);

        this.inventory = new LinkedList<>();
        this.inventory.offer(buyTransaction1);
        this.inventory.offer(buyTransaction2);
    }

    @Test
    void getNext_shouldConsumeFullLotAndCalculatePnl() {
        Transaction sellTransaction = new Transaction("", 3L, "", Type.Sell, 10.0f, 3.0f, 30.0f, "", 1.0f);
        SellSummaryWorker worker = new SellSummaryWorker(this.inventory);
        SellSummary summary = worker.getNext(sellTransaction);

        float sellQuantity = 10.0f; // Quantity of the sellTransaction
        float grossProceeds = 30.0f; // Amount of the sellTransaction
        float costBasis = 10.0f; // Amount of the buyTransaction1
        float pnl = grossProceeds - costBasis;

        Assertions.assertEquals(
                sellQuantity, summary.quantity(), 0.01f, "Sold shares must match sellTransaction quantity");
        Assertions.assertEquals(costBasis, summary.costBasis(), 0.01f, "Sold shares must match buyTransaction1 amount");
        Assertions.assertEquals(
                grossProceeds, summary.grossProceeds(), 0.01f, "Gross proceeds must match sellTransaction amount");
        Assertions.assertEquals(pnl, summary.pnl(), 0.01f, "Calculated pnl is incorrect");

        Assertions.assertEquals(1, inventory.size(), "Inventory size must be reduced by one lot");
        Assertions.assertEquals(10.0f, inventory.peek().quantity(), 0.01f, "The remaining lot must be untouched");
    }

    @Test
    void getNext_shouldPartiallyConsumeLotAndLeaveRemainder() {
        Transaction sellTransaction = new Transaction("", 3L, "", Type.Sell, 5.0f, 3.0f, 15.0f, "", 1.0f);
        SellSummaryWorker worker = new SellSummaryWorker(this.inventory);
        worker.getNext(sellTransaction);

        Assertions.assertEquals(2, this.inventory.size(), "Inventory size must remain 2 (updated lot + second lot)");

        Transaction firstLot = this.inventory.poll();
        Transaction secondLot = this.inventory.poll();

        Assertions.assertEquals(5.0f, firstLot.quantity(), 0.01f, "Remaining shares in the first lot must be 5");
        Assertions.assertEquals(
                10f,
                secondLot.quantity(),
                0.01f,
                "The second lot must be untouched and still be in the second position");
    }
}
