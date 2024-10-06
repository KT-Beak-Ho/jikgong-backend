package jikgong.global.utils;

import java.util.Random;

public class RandomCode {

    // 6자리 랜덤 코드 생성
    public static String createAuthCode() {
        String characters = "0123456789";

        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder(6); // 6자리 랜덤 코드

        // 지정된 길이만큼 랜덤 코드 생성
        for (int i = 0; i < 6; i++) {
            // 랜덤으로 문자 선택
            char randomChar = characters.charAt(random.nextInt(characters.length()));
            codeBuilder.append(randomChar);
        }
        return codeBuilder.toString();
    }

    // 임시 비밀번호 생성
    public static String createTemporaryPassword() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder tempPassword = new StringBuilder();
        for (int i = 0; i < length; i++) {
            tempPassword.append(chars.charAt(random.nextInt(chars.length())));
        }
        return tempPassword.toString();
    }
}
