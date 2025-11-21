package net.grinv.revinvest;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.consts.RequestParams;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.model.Report;
import net.grinv.revinvest.repository.TransactionRepository;
import net.grinv.revinvest.service.ReportService;
import net.grinv.revinvest.utils.DateTimeUtils;

@WebServlet("")
public final class IndexServlet extends HttpServlet {
    private final ReportService reportService;

    public IndexServlet() {
        TransactionRepository repo = new TransactionRepository();
        this.reportService = new ReportService(repo);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fromParam = request.getParameter(RequestParams.FROM);
            String toParam = request.getParameter(RequestParams.TO);
            String symbolParam = request.getParameter(RequestParams.SYMBOL);
            String currencyParam = request.getParameter(RequestParams.CURRENCY);

            Filter filter = new Filter(fromParam, toParam, symbolParam, Currency.getCurrencyByString(currencyParam));
            Report report = this.reportService.generate(filter);

            request.setAttribute("generationDate", DateTimeUtils.getCurrentDate());
            request.setAttribute("report", report);
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        } catch (Exception error) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
