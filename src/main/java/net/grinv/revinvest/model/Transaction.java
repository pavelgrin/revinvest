package net.grinv.revinvest.model;

import net.grinv.revinvest.consts.Type;

public record Transaction(
        String date,
        long timestamp,
        String ticker,
        Type type,
        float quantity,
        float pricePerShare,
        float amount,
        String currency,
        float fxRate) {}
