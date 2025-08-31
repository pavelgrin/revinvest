package net.grinv.revinvest.model;

public record Dividends(
    int amount,
    int withTax,
    int tax)
{
}
