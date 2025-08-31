package net.grinv.revinvest.model;

import net.grinv.revinvest.consts.Currency;

public record QueryParams(
    String from,
    String to,
    String symbol,
    Currency currency)
{
}
