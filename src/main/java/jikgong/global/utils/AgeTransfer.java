package jikgong.global.utils;

import java.time.LocalDateTime;

public class AgeTransfer {
    public static int getAgeByRrn(String rrnPrefix) {
        int birthYear = Integer.parseInt(rrnPrefix.substring(0, 4));
        int currentYear = LocalDateTime.now().getYear();
        return currentYear - birthYear + 1;
    }
}
