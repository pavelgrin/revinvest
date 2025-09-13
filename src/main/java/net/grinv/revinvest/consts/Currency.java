package net.grinv.revinvest.consts;

public enum Currency
{
    USD("USD"),
    EUR("EUR");

    private final String code;

    Currency(String code) { this.code = code; }
    public String getCode() { return code; }
}
