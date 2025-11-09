package net.grinv.revinvest.service;

import java.util.Collections;
import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.model.Report;
import net.grinv.revinvest.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
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
        Mockito.when(mockRepository.getAllTransactions()).thenReturn(Collections.emptyList());

        Filter filter = new Filter("", "", "", Currency.getCurrencyByString(null));
        Report report = reportService.generate(filter);

        Assertions.assertNotNull(report, "The Report object shouldn't be null");
    }
}
