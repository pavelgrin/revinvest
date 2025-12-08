package net.grinv.revinvest.model;

public record SellSummary(String date, String symbol, float quantity, float costBasis, float grossProceeds, float pnl) {
    public String getQuantityFixed() {
        return String.format("%.8f", this.quantity);
    }

    public String getCostBasisFixed() {
        return String.format("%.2f", this.costBasis);
    }

    public String getGrossProceedsFixed() {
        return String.format("%.2f", this.grossProceeds);
    }

    public String getPnlFixed() {
        return String.format("%.2f", this.pnl);
    }
}
