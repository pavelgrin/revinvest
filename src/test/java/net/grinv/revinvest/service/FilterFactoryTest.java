package net.grinv.revinvest.service;

import net.grinv.revinvest.consts.Currency;
import net.grinv.revinvest.model.Filter;
import net.grinv.revinvest.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FilterFactoryTest {
    @Mock
    private TransactionRepository mockRepository;

    @InjectMocks
    private FilterFactory factory;

    @Test
    @DisplayName("All Params Provided: Should return exact input values")
    void build_shouldReturnExactInput_whenAllParamsAreProvided() {
        String from = "2025-06-01";
        String to = "2025-06-30";
        String symbol = "TSLA";
        String currency = "EUR";

        Filter filter = factory.build(from, to, symbol, currency);

        Assertions.assertEquals(from, filter.from(), "The \"from\" date must match the input");
        Assertions.assertEquals(to, filter.to(), "The \"to\" date must match the input");
        Assertions.assertEquals(symbol, filter.symbol(), "The symFUSbol must match the input");
        Assertions.assertEquals(Currency.EUR, filter.currency(), "The currency must be correctly mapped");

        // Verify that the repository was NOT called, as dates were provided
        Mockito.verify(mockRepository, Mockito.never()).getFirstTransactionDate();
    }

    @Test
    @DisplayName("Missing Dates: Should use MIN/MAX dates from Repository")
    void build_shouldUseRepositoryDates_whenDateParamsAreNull() {
        // Configure the mock to return predictable default dates for all tests
        String defaultFrom = "2022-01-01";
        String defaultTo = "2025-12-31";
        Mockito.when(mockRepository.getFirstTransactionDate()).thenReturn(defaultFrom);
        Mockito.when(mockRepository.getLastTransactionDate()).thenReturn(defaultTo);

        Filter filter = factory.build(null, null, "", "");

        Assertions.assertEquals(defaultFrom, filter.from(), "The \"from\" date must default to repository MIN date");
        Assertions.assertEquals(defaultTo, filter.to(), "The \"to\" date must default to repository MAX date");

        // Verify that the repository was called to fetch the defaults
        Mockito.verify(mockRepository, Mockito.times(1)).getFirstTransactionDate();
        Mockito.verify(mockRepository, Mockito.times(1)).getLastTransactionDate();
    }

    @Test
    @DisplayName("Missing Ticker: Should set symbol to null")
    void build_shouldSetSymbolToNull_whenSymbolIsBlankOrNull() {
        Filter filter1 = factory.build("", "", null, "");
        Filter filter2 = factory.build("", "", "  \t  ", "");

        Assertions.assertNull(filter1.symbol(), "Symbol must be null when input is null");
        Assertions.assertNull(filter2.symbol(), "Symbol must be null when input is whitespace/blank");
        Assertions.assertFalse(filter1.hasTicker(), "hasTicker() must return false for null symbol");
    }

    @Test
    @DisplayName("Missing Currency: Should default to Currency.ORIGINAL")
    void build_shouldDefaultCurrency_whenCurrencyParamIsBlank() {
        Filter filter = factory.build("", "", "", "");
        Assertions.assertEquals(Currency.ORIGINAL, filter.currency(), "Currency must default to ORIGINAL");
    }

    @Test
    @DisplayName("Invalid Currency: Should propagate exception from utility")
    void build_shouldPropagateException_whenCurrencyIsInvalid() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> factory.build("", "", "", "RANDOM STRING"),
                "Invalid currency input must trigger an exception");
    }
}
