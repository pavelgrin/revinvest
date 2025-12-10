package net.grinv.revinvest.model;

import java.util.List;

public record TickerReport(float dividends, TickerSummary summary, float pnlTotal, List<SellSummary> sellsSummary) {
    public String getDividendsFixed() {
        return String.format("%.2f", this.dividends);
    }

    public TickerSummary getSummary() {
        return this.summary;
    }

    public String getPnlTotalFixed() {
        return String.format("%.2f", this.pnlTotal);
    }

    public List<SellSummary> getSellsSummary() {
        return this.sellsSummary;
    }
}
