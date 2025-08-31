package net.grinv.revinvest.model;

import java.util.ArrayList;

public final class TickerReport
{
    private int dividends;
    private TickerSummary summary;
    private int pnlTotal;
    private ArrayList<SummaryItem> sellsSummary;

    public int getDividends() { return this.dividends; }
    public void setDividends(int value) { this.dividends = value; }

    public TickerSummary getSummary() { return this.summary; }
    public void setSummary(TickerSummary value) { this.summary = value; }

    public int getPnlTotal() { return this.pnlTotal; }
    public void setPnlTotal(int value) { this.pnlTotal = value; }

    public ArrayList<SummaryItem> getSellsSummary() { return this.sellsSummary; }
    public void setSellsSummary(ArrayList<SummaryItem> value) { this.sellsSummary = value; }
}
