package net.grinv.revinvest.model;

public record TickerSummary(float buyAmount, float sellAmount, float netAmount, float quantity, float bep) {
    public String getBuyAmountFixed() {
        return String.format("%.2f", this.buyAmount);
    }

    public String getSellAmountFixed() {
        return String.format("%.2f", this.sellAmount);
    }

    public String getNetAmountFixed() {
        return String.format("%.2f", this.netAmount);
    }

    public String getQuantityFixed() {
        return String.format("%.8f", this.quantity);
    }

    public String getBepFixed() {
        return String.format("%.8f", this.bep);
    }
}
