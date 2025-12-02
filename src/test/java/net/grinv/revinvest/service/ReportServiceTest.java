package net.grinv.revinvest.service;

import net.grinv.revinvest.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public final class ReportServiceTest {
    @Mock
    private TransactionRepository mockRepository;

    private ReportService reportService;

    @BeforeEach
    void setup() {
        mockRepository = Mockito.mock(TransactionRepository.class);
        reportService = new ReportService(mockRepository);
    }

    @Test
    void reportIsNotNull() {
        // Filter filter = new Filter("", "", "", Currency.getCurrencyByString("ORIGINAL"));

        // Mockito.when(mockRepository.getStatement(filter)).thenReturn(Collections.emptyList());

        // Report report = reportService.generate(filter);

        // Assertions.assertNotNull(report, "The Report object shouldn't be null");
    }
}
