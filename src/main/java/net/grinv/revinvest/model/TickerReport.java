package net.grinv.revinvest.model;

import java.util.ArrayList;

public final class TickerReport {
    private float dividends;
    private TickerSummary summary;
    private int pnlTotal;
    private ArrayList<SellSummary> sellsSummary;

    public float getDividends() {
        return this.dividends;
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

    public int getPnlTotal() {
        return this.pnlTotal;
    }

    public void setPnlTotal(int value) {
        this.pnlTotal = value;
    }

    public ArrayList<SellSummary> getSellsSummary() {
        return this.sellsSummary;
    }

    public void setSellsSummary(ArrayList<SellSummary> value) {
        this.sellsSummary = value;
    }
}
