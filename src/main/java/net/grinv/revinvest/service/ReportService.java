package net.grinv.revinvest.service;

import net.grinv.revinvest.model.CommonReport;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.model.Report;
import net.grinv.revinvest.model.TickerReport;

public class ReportService
{
    public Report generate(Filter filter)
    {
        Report report = new Report();

        report.setFilter(filter);

        if (filter.hasTicker())
        {
            report.setTickerReport(new TickerReport());
        }
        else
        {
            report.setCommonReport(new CommonReport());
        }

        return report;
    }
}
