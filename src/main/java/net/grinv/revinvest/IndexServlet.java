package net.grinv.revinvest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.RequestParams;
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
        String fromParam = request.getParameter(RequestParams.FROM);
        String toParam = request.getParameter(RequestParams.TO);
        String symbolParam = request.getParameter(RequestParams.SYMBOL);
        String currencyParam = request.getParameter(RequestParams.CURRENCY);

        // TODO: Not safe to call valueOf here
        // Use values() or getEnumConstants and check currencyParam
        Currency currency = currencyParam != null && !currencyParam.isEmpty()
                ? Currency.valueOf(currencyParam.toUpperCase())
                : null;

        BaseReport baseReport = new BaseReport();
        baseReport.setFrom(fromParam);
        baseReport.setTo(toParam);
        baseReport.setCurrency(currency == null ? Currency.USD : currency);
        baseReport.setSymbol(symbolParam == null || symbolParam.isEmpty() ? null : symbolParam);

        request.setAttribute("baseReport", baseReport);

        if (symbolParam == null || symbolParam.isEmpty())
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
