package net.grinv.revinvest.model;

public record SummaryItem(String date, String symbol, float quantity, int costBasis, int grossProceeds, int pnl) {
    public String getQuantityFixed() {
        return String.format("%.8f", this.quantity);
    }
}
