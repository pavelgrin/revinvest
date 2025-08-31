package net.grinv.revinvest.model;

import java.util.ArrayList;

public final class CommonReport
{
    private int balance;
    private Dividends dividends;
    private int custodyFee;
    private int totalFIFO;
    private int totalLIFO;
    private ArrayList<SummaryItem> summaryFIFO;
    private ArrayList<SummaryItem> summaryLIFO;

    public int getBalance() { return this.balance; }
    public void setBalance(int value) { this.balance = value; }

    public Dividends getDividends() { return this.dividends; }
    public void setDividends(Dividends value) { this.dividends = value; }

    public int getCustodyFee() { return this.custodyFee; }
    public void setCustodyFee(int value) { this.custodyFee = value; }

    public int getTotalFIFO() { return this.totalFIFO; }
    public void setTotalFIFO(int value) { this.totalFIFO = value; }

    public int getTotalLIFO() { return this.totalLIFO; }
    public void setTotalLIFO(int value) { this.totalLIFO = value; }

    public ArrayList<SummaryItem> getSummaryFIFO() { return this.summaryFIFO; }
    public void setSummaryFIFO(ArrayList<SummaryItem> value) { this.summaryFIFO = value; }

    public ArrayList<SummaryItem> getSummaryLIFO() { return this.summaryLIFO; }
    public void setSummaryLIFO(ArrayList<SummaryItem> value) { this.summaryLIFO = value; }
}
