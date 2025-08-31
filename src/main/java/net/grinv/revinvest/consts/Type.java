package net.grinv.revinvest.consts;

public enum Type
{
    TopUp("CASH TOP-UP"),
    Withdraw("CASH WITHDRAWAL"),
    Dividend("DIVIDEND"),
    Buy("BUY - MARKET"),
    Sell("SELL - MARKET"),
    CustodyFee("CUSTODY FEE"),
    StockSplit("STOCK SPLIT"),
    Unknown("");

    private final String label;

    Type(String label)
    {
        this.label = label;
    }

    public String getLabel() { return label; }
}
