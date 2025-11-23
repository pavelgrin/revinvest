package net.grinv.revinvest.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateTimeUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'H:mm:ss[.SSS]'Z'");

    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMAT);
    }

    public static String getDate(String isoDateTime) {
        LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, INPUT_FORMAT);
        return dateTime.toLocalDate().format(DATE_FORMAT);
    }

    public static String getTime(String isoDateTime) {
        LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, INPUT_FORMAT);
        return dateTime.toLocalTime().format(TIME_FORMAT);
    }

    public static String getDateTime(String isoDateTime) {
        LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, INPUT_FORMAT);
        return dateTime.format(DATE_TIME_FORMAT);
    }

    /**
     * Converts iso date-time string into a millisecond timestamp by consistently assuming the input string represents
     * UTC time.
     * @param isoDateTime date-time string (e.g., {@code 1970-01-01T00:00:00.000Z})
     * @return millisecond timestamp ({@code long})
     */
    public static long getTimestamp(String isoDateTime) {
        LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, INPUT_FORMAT);
        return dateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }
}
