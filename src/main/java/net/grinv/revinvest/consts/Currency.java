package net.grinv.revinvest.consts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Currency {
    USD("USD"),
    EUR("EUR");

    private static final Logger logger = LoggerFactory.getLogger(Currency.class);

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
     * @param currencyStr string from the request
     * @return corresponding Currency enum
     */
    public static Currency getCurrencyByString(String currencyStr) {
        if (currencyStr == null || currencyStr.isEmpty()) {
            return Currency.USD;
        }

        try {
            return Currency.valueOf(currencyStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid currencyStr value: {}", currencyStr);
            return Currency.USD;
        }
    }
}
