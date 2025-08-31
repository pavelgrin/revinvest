package net.grinv.revinvest.model;

import net.grinv.revinvest.consts.Currency;

public record Filter(
    String from,
    String to,
    String symbol,
    Currency currency)
{
}
