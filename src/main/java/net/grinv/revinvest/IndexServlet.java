package net.grinv.revinvest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.RequestParams;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.model.Report;
import net.grinv.revinvest.service.ReportService;

import java.io.IOException;

@WebServlet("")
public final class IndexServlet extends HttpServlet
{
    private final ReportService reportService = new ReportService();

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

        Filter filter = new Filter(fromParam, toParam, symbolParam, currency);
        Report report = this.reportService.generate(filter);

        // TODO: Format date time output
        request.setAttribute("generationDate", java.time.LocalDateTime.now().toString());
        request.setAttribute("report", report);
        request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
    }
}
