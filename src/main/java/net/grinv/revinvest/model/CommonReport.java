package net.grinv.revinvest.model;

import java.util.ArrayList;

public final class CommonReport {
    private float balance;
    private Dividends dividends;
    private float custodyFee;
    private int totalFIFO;
    private int totalLIFO;
    private ArrayList<SummaryItem> summaryFIFO;
    private ArrayList<SummaryItem> summaryLIFO;

    public String getBalanceFixed() {
        return String.format("%.2f", this.balance);
    }

    public void setBalance(float value) {
        this.balance = value;
    }

    public Dividends getDividends() {
        return this.dividends;
    }

    public void setDividends(Dividends value) {
        this.dividends = value;
    }

    public String getCustodyFeeFixed() {
        return String.format("%.2f", this.custodyFee);
    }

    public void setCustodyFee(float value) {
        this.custodyFee = value;
    }

    public int getTotalFIFO() {
        return this.totalFIFO;
    }

    public void setTotalFIFO(int value) {
        this.totalFIFO = value;
    }

    public int getTotalLIFO() {
        return this.totalLIFO;
    }

    public void setTotalLIFO(int value) {
        this.totalLIFO = value;
    }

    public ArrayList<SummaryItem> getSummaryFIFO() {
        return this.summaryFIFO;
    }

    public void setSummaryFIFO(ArrayList<SummaryItem> value) {
        this.summaryFIFO = value;
    }

    public ArrayList<SummaryItem> getSummaryLIFO() {
        return this.summaryLIFO;
    }

    public void setSummaryLIFO(ArrayList<SummaryItem> value) {
        this.summaryLIFO = value;
    }
}
