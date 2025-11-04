package net.grinv.revinvest.model;

public final class Report {
    private Filter filter;
    private CommonReport commonReport;
    private TickerReport tickerReport;

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public CommonReport getCommonReport() {
        return this.commonReport;
    }

    public void setCommonReport(CommonReport report) {
        this.commonReport = report;
    }

    public TickerReport getTickerReport() {
        return this.tickerReport;
    }

    public void setTickerReport(TickerReport report) {
        this.tickerReport = report;
    }
}
