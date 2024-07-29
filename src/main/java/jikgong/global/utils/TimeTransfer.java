package jikgong.global.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static String getTimeDifference(LocalDateTime localDateTime) {
        LocalDateTime currentTime = LocalDateTime.now();

        Duration duration = Duration.between(localDateTime, currentTime);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        long months = days / 30;
        long years = days / 365;

        if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days < 30) {
            return days + "일 전";
        } else if (months < 12) {
            return months + "달 전";
        } else {
            return years + "년 전";
        }
    }
}
