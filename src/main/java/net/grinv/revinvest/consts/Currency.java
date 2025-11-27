package net.grinv.revinvest.consts;

public enum Currency {
    ORIGINAL("ORIGINAL"),
    EUR("EUR");

    private final String code;

    Currency(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Converts currency string to Currency enum or returns default value
     *
     * @param currencyStr string that must represent one of the enum items
     * @return corresponding Currency enum
     */
    public static Currency getCurrencyByString(String currencyStr) {
        return Currency.valueOf(currencyStr.toUpperCase());
    }
}
