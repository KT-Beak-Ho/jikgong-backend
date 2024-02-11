package jikgong.global.utils;

import java.time.LocalDateTime;

public class AgeTransfer {
    public static int getAgeByBirth(String birth) {
        int birthYear = Integer.parseInt(birth.substring(0, 4));
        int currentYear = LocalDateTime.now().getYear();
        return currentYear - birthYear + 1;
    }
}
