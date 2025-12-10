package net.grinv.revinvest.model;

import java.util.List;

public record CommonReport(
        float balance,
        Dividends dividends,
        float custodyFee,
        float totalFIFO,
        float totalLIFO,
        List<SellSummary> summaryFIFO,
        List<SellSummary> summaryLIFO) {
    public String getBalanceFixed() {
        return String.format("%.2f", this.balance);
    }

    public Dividends getDividends() {
        return this.dividends;
    }

    public String getCustodyFeeFixed() {
        return String.format("%.2f", this.custodyFee);
    }

    public String getTotalFIFOFixed() {
        return String.format("%.2f", this.totalFIFO);
    }

    public String getTotalLIFOFixed() {
        return String.format("%.2f", this.totalLIFO);
    }

    public List<SellSummary> getSummaryFIFO() {
        return this.summaryFIFO;
    }

    public List<SellSummary> getSummaryLIFO() {
        return this.summaryLIFO;
    }
}
