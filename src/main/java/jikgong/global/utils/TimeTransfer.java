package jikgong.global.utils;

import java.time.LocalDate;
import java.time.YearMonth;

public class TimeTransfer {

    public static LocalDate getFirstDayOfMonth(LocalDate localDate) {
        return localDate.withDayOfMonth(1);
    }

    public static LocalDate getLastDayOfMonth(LocalDate localDate) {
        return localDate.withDayOfMonth(YearMonth.from(localDate).lengthOfMonth());
    }

    public static LocalDate getFirstDayOfYear(LocalDate localDate) {
        return localDate.withDayOfYear(1);
    }

    public static LocalDate getLastDayOfYear(LocalDate localDate) {
        return localDate.withDayOfYear(localDate.lengthOfYear());
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
