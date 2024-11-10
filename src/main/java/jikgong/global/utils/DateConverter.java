package jikgong.global.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter {

    public static LocalDate convertToLocalDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(dateString, formatter);
    }
}
