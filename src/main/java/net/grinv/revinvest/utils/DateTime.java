package net.grinv.revinvest.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateTime {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMAT);
    }
}
