package net.grinv.revinvest;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.grinv.revinvest.consts.RequestParams;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.model.Report;
import net.grinv.revinvest.repository.TransactionRepository;
import net.grinv.revinvest.service.FilterFactory;
import net.grinv.revinvest.service.ReportService;
import net.grinv.revinvest.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("")
public final class IndexServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(IndexServlet.class);

    private final ReportService reportService;
    private final FilterFactory filterFactory;

    public IndexServlet() {
        TransactionRepository repo = new TransactionRepository();
        this.reportService = new ReportService(repo);
        this.filterFactory = new FilterFactory(repo);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fromParam = request.getParameter(RequestParams.FROM);
            String toParam = request.getParameter(RequestParams.TO);
            String symbolParam = request.getParameter(RequestParams.SYMBOL);
            String currencyParam = request.getParameter(RequestParams.CURRENCY);

            // TODO: Currently, the report is calculated for every request. We need to write a service that will trigger
            // when the raw transaction list is updated, generate the report, and cache the result
            Filter filter = this.filterFactory.build(fromParam, toParam, symbolParam, currencyParam);
            Report report = this.reportService.generate(filter);

            request.setAttribute("generationDate", DateTimeUtils.getCurrentDateTime());
            request.setAttribute("report", report);
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        } catch (Exception error) {
            logger.error("IndexServlet error", error);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
