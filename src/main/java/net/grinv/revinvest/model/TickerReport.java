package net.grinv.revinvest.model;

import java.util.List;

public final class TickerReport {
    private float dividends;
    private TickerSummary summary;
    private float pnlTotal;
    private List<SellSummary> sellsSummary;

    public String getDividendsFixed() {
        return String.format("%.2f", this.dividends);
    }

    public void setDividends(float value) {
        this.dividends = value;
    }

    public TickerSummary getSummary() {
        return this.summary;
    }

    public void setSummary(TickerSummary value) {
        this.summary = value;
    }

    public String getPnlTotalFixed() {
        return String.format("%.2f", this.pnlTotal);
    }

    public void setPnlTotal(float value) {
        this.pnlTotal = value;
    }

    public List<SellSummary> getSellsSummary() {
        return this.sellsSummary;
    }

    public void setSellsSummary(List<SellSummary> value) {
        this.sellsSummary = value;
    }
}
