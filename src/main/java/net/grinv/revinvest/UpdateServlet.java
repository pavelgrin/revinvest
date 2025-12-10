package net.grinv.revinvest;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.InputStream;
import java.util.List;
import net.grinv.revinvest.model.Transaction;
import net.grinv.revinvest.repository.TransactionRepository;
import net.grinv.revinvest.service.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/update")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 10, // 10MB
        maxFileSize = 1024 * 1024 * 10) // 10MB
public final class UpdateServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UpdateServlet.class);

    private final TransactionRepository transactionRepository;
    private final Parser parser;

    public UpdateServlet() {
        this.transactionRepository = new TransactionRepository();
        this.parser = new Parser();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Part filePart = request.getPart("statement");
            if (filePart == null || filePart.getSize() == 0) {
                throw new RuntimeException("Uploadable CSV file is empty");
            }

            try (InputStream inputStream = filePart.getInputStream()) {
                List<Transaction> transactions = this.parser.parseCSVReport(inputStream);
                if (transactions.isEmpty()) {
                    throw new RuntimeException("The CSV file doesn't contain transaction records");
                }

                transactionRepository.updateStatement(transactions);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception error) {
            logger.error("[doPost] UpdateServlet error", error);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
