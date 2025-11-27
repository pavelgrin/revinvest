package net.grinv.revinvest.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'H:mm:ss[.[SSSSSS][SSS]]'Z'");

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMAT);
    }

    public static String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMAT);
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
     * UTC time
     *
     * @param isoDateTime date-time string (e.g., {@code 1970-01-01T00:00:00.000[000]Z})
     * @return millisecond timestamp ({@code long})
     */
    public static long getTimestamp(String isoDateTime) {
        LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, INPUT_FORMAT);
        return dateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    public static String getDateByTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneId.of("UTC")).toLocalDate().format(DATE_FORMAT);
    }

    /**
     * Converts YYYY-MM-DD date string to the timestamp for the start of the specified calendar day (00:00:00)
     *
     * @param dateString date (e.g., "1970-01-01")
     * @return the timestamp of the specified day's midnight
     */
    public static long getTimestampByDate(String dateString) {
        LocalDate date = LocalDate.parse(dateString, DATE_FORMAT);
        return date.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    /**
     * Converts YYYY-MM-DD date string to the timestamp for the start of the next calendar day (00:00:00)
     *
     * @param dateString date (e.g., "1970-01-01")
     * @return the timestamp of the next day's midnight
     */
    public static long getNextDayTimestampByDate(String dateString) {
        LocalDate date = LocalDate.parse(dateString, DATE_FORMAT);
        LocalDate nextDay = date.plusDays(1);
        return nextDay.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }
}
