package net.grinv.revinvest.model;

import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.Type;

public record Transaction(
        String isoDate,
        String date,
        long timestamp,
        String ticker,
        Type type,
        float quantity,
        float pricePerShare,
        float amount,
        Currency currency,
        float fxRate) {}
