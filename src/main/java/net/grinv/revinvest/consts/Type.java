package net.grinv.revinvest.consts;

public enum Type {
    TopUp("CASH TOP-UP"),
    Withdraw("CASH WITHDRAWAL"),
    Dividend("DIVIDEND"),
    Buy("BUY - MARKET"),
    Sell("SELL - MARKET"),
    CustodyFee("CUSTODY FEE"),
    StockSplit("STOCK SPLIT"),
    Unknown("");

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
     * @param typeStr string from the request
     * @return corresponding Type enum
     */
    public static Type getTypeByString(String typeStr) {
        if (typeStr == null || typeStr.isEmpty()) {
            return Type.Unknown;
        }

        try {
            return Type.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            // TODO: We shouldn't be here, log this line
            return Type.Unknown;
        }
    }
}
