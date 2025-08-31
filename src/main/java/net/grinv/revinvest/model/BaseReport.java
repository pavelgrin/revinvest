package net.grinv.revinvest.model;

import net.grinv.revinvest.consts.Currency;

public final class BaseReport
{
    private String from;
    private String to;
    private String symbol;
    private Currency currency;

    public String getFrom() { return this.from; }
    public void setFrom(String value) { this.from = value; }

    public String getTo() { return this.to; }
    public void setTo(String value) { this.to = value; }

    public String getSymbol() { return this.symbol; }
    public void setSymbol(String value) { this.symbol = value; }

    public Currency getCurrency() { return this.currency; }
    public void setCurrency(Currency value) { this.currency = value;}
}
