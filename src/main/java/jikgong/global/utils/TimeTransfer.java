package jikgong.global.utils;

import java.time.LocalDateTime;
import java.time.YearMonth;

public class TimeTransfer {
    public static LocalDateTime getFirstDayOfMonth(LocalDateTime localDateTime) {
        return localDateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime getLastDayOfMonth(LocalDateTime localDateTime) {
        return localDateTime.withDayOfMonth(YearMonth.from(localDateTime).lengthOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(0);
    }

    public static LocalDateTime getFirstTimeOfDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime getLastTimeOfDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0);
    }
}
