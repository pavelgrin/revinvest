package net.grinv.revinvest.model;

import java.util.List;

public final class CommonReport {
    private float balance;
    private Dividends dividends;
    private float custodyFee;
    private float totalFIFO;
    private float totalLIFO;
    private List<SellSummary> summaryFIFO;
    private List<SellSummary> summaryLIFO;

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

    public String getTotalFIFOFixed() {
        return String.format("%.2f", this.totalFIFO);
    }

    public void setTotalFIFO(float value) {
        this.totalFIFO = value;
    }

    public String getTotalLIFOFixed() {
        return String.format("%.2f", this.totalLIFO);
    }

    public void setTotalLIFO(float value) {
        this.totalLIFO = value;
    }

    public List<SellSummary> getSummaryFIFO() {
        return this.summaryFIFO;
    }

    public void setSummaryFIFO(List<SellSummary> value) {
        this.summaryFIFO = value;
    }

    public List<SellSummary> getSummaryLIFO() {
        return this.summaryLIFO;
    }

    public void setSummaryLIFO(List<SellSummary> value) {
        this.summaryLIFO = value;
    }
}
