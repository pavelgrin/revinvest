package net.grinv.revinvest.consts;

import java.util.Arrays;

public enum Type {
    TopUp("CASH TOP-UP"),
    Withdraw("CASH WITHDRAWAL"),
    Dividend("DIVIDEND"),
    Buy("BUY - MARKET"),
    Sell("SELL - MARKET"),
    CustodyFee("CUSTODY FEE"),
    StockSplit("STOCK SPLIT");

    private final String label;

    Type(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Converts type string to Type enum
     *
     * @param typeStr string that must represent one of the enum items
     * @return corresponding Type enum
     */
    public static Type getTypeByString(String typeStr) {
        if (typeStr == null || typeStr.isBlank()) {
            throw new IllegalArgumentException("Type cannot be blank");
        }
        return Arrays.stream(Type.values())
                .filter((t) -> t.label.equalsIgnoreCase(typeStr.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum constant with value: " + typeStr));
    }
}
