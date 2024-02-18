package jikgong.global.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

public class TimeTransfer {
    public static LocalDateTime getFirstDayOfMonth(LocalDateTime localDateTime) {
        return localDateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime getLastDayOfMonth(LocalDateTime localDateTime) {
        return localDateTime.withDayOfMonth(YearMonth.from(localDateTime).lengthOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(0);
    }

    public static LocalDate getFirstDayOfMonth(LocalDate localDate) {
        return localDate.withDayOfMonth(1);
    }

    public static LocalDate getLastDayOfMonth(LocalDate localDate) {
        return localDate.withDayOfMonth(YearMonth.from(localDate).lengthOfMonth());
    }

    public static LocalDateTime getFirstTimeOfDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime getLastTimeOfDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0);
    }

    public static String getHourMinute(Integer minute) {
        if (minute == null) {
            return "0시간 0분";
        }
        int hours = minute / 60;
        int remainingMinutes = minute % 60;
        return hours + "시간 " + remainingMinutes + "분";
    }
}
