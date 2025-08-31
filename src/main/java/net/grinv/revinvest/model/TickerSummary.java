package net.grinv.revinvest.model;

public record TickerSummary(
    int buy,
    int sell,
    int netAmount,
    float quantity,
    int bep)
{
    public String getQuantityFixed() {
        return String.format("%.8f", this.quantity);
    }
}
