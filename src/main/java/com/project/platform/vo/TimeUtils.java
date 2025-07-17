package com.project.platform.vo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeUtils {
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<LocalDateTime> getRecentSevenDays(Integer num) {
        List<LocalDateTime> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Starting from today, get the dates of the previous 'num' days
        LocalDateTime today = LocalDateTime.now();
        for (int i = 0; i < num; i++) {
            LocalDateTime date = today.plusDays(-i);
            String formattedDate = date.format(formatter);
            dates.add(date);
        }
        // Reverse the list so that the latest date is at the end of the list
        java.util.Collections.reverse(dates);
        return dates;
    }

    public static String formatterDate(LocalDateTime localDateTime) {
        return formatterDate(localDateTime, FORMATTER_DATE);
    }

    public static String formatterDate(LocalDateTime localDateTime, String formatter) {
        return formatterDate(localDateTime, DateTimeFormatter.ofPattern(formatter));
    }

    public static String formatterDate(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);
    }

    public static String formatterTime(LocalDateTime localDateTime) {
        return formatterTime(localDateTime, FORMATTER_TIME);

    }

    public static String formatterTime(LocalDateTime localDateTime, String formatter) {
        return formatterTime(localDateTime, DateTimeFormatter.ofPattern(formatter));
    }

    public static String formatterTime(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);
    }

    /**
     * Set the LocalDateTime to the start of the day (midnight)
     *
     * @param dateTime the input LocalDateTime object
     * @return LocalDateTime set to midnight of the same day
     */
    public static LocalDateTime setToMidnight(LocalDateTime dateTime) {
        return dateTime.withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    /**
     * Set the LocalDateTime to the end of the day (23:59:59)
     *
     * @param dateTime the input LocalDateTime object
     * @return LocalDateTime set to 23:59:59 of the same day
     */
    public static LocalDateTime setToEndOfDay(LocalDateTime dateTime) {
        return dateTime.withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(59);
    }
}
