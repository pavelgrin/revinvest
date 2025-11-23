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
import net.grinv.revinvest.utils.Parser;

@WebServlet("/update")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10 // 10MB
)
public final class UpdateServlet extends HttpServlet {
    private final TransactionRepository transactionRepository = new TransactionRepository();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Part filePart = request.getPart("statement");
            if (filePart == null || filePart.getSize() == 0) {
                throw new RuntimeException();
            }

            try (InputStream inputStream = filePart.getInputStream()) {
                List<Transaction> transactions = Parser.parseCSVReport(inputStream);
                if (transactions.isEmpty()) {
                    throw new RuntimeException();
                }

                transactionRepository.updateStatement(transactions);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception error) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
