package net.grinv.revinvest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.model.BaseReport;
import net.grinv.revinvest.model.CommonReport;
import net.grinv.revinvest.model.TickerReport;

import java.io.IOException;

@WebServlet("")
public final class IndexServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        BaseReport baseReport = new BaseReport();
        baseReport.setFrom("2025-01-01");
        baseReport.setTo("2025-12-31");
        baseReport.setSymbol("");
        baseReport.setCurrency(Currency.USD);

        request.setAttribute("baseReport", baseReport);

        if (baseReport.getSymbol().isEmpty())
        {
            CommonReport commonReport = new CommonReport();
            request.setAttribute("commonReport", commonReport);
        }
        else
        {
            TickerReport tickerReport = new TickerReport();
            request.setAttribute("tickerReport", tickerReport);
        }

        // TODO: Format date time output
        request.setAttribute("generationDate", java.time.LocalDateTime.now().toString());
        request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
    }
}
