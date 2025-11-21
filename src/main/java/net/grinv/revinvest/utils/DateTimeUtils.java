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
    // TODO: Fix INPUT_FORMAT according actual data
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'H:mm:ss[.SSS]");

    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMAT);
    }

    public static String getDate(String isoDateTime) {
        LocalDateTime dateTime = safeParse(isoDateTime);
        return dateTime != null ? dateTime.toLocalDate().format(DATE_FORMAT) : null;
    }

    public static String getTime(String isoDateTime) {
        LocalDateTime dateTime = safeParse(isoDateTime);
        return dateTime != null ? dateTime.toLocalTime().format(TIME_FORMAT) : null;
    }

    public static String getDateTime(String isoDateTime) {
        LocalDateTime dateTime = safeParse(isoDateTime);
        return dateTime != null ? dateTime.format(DATE_TIME_FORMAT) : null;
    }

    public static long getTimestampByDate(String isoDateTime) {
        return Instant.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Converts iso date-time string into a millisecond timestamp by consistently assuming the input string represents
     * UTC time.
     *
     * @param isoDateTime date-time string (e.g., "2025-11-12T9:58:11")
     * @return millisecond timestamp (long), or 0 if parsing fails
     */
    public static long getTimestampByDateTimeString(String isoDateTime) {
        LocalDateTime dateTime = safeParse(isoDateTime);
        if (dateTime == null) {
            return 0;
        }
        return dateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    private static LocalDateTime safeParse(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(isoDateTime, INPUT_FORMAT);
        } catch (DateTimeException error) {
            logger.error("Could not parse date-time string: {}", isoDateTime, error);
            return null;
        }
    }
}
