package net.grinv.revinvest.model;

public record Dividends(float amount, float withTax, float tax) {
    public String getAmountFixed() {
        return String.format("%.2f", this.amount);
    }

    public String getWithTaxFixed() {
        return String.format("%.2f", this.withTax);
    }

    public String getTaxFixed() {
        return String.format("%.2f", this.tax);
    }
}
