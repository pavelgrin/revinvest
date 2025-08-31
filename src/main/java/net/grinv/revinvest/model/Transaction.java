package net.grinv.revinvest.model;

import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.Type;

public record Transaction(
    String isoDate,
    String date,
    int timestamp,
    String ticker,
    Type type,
    int quantity,
    int pricePerShare,
    int amount,
    Currency currency,
    int fxRate)
{
}
