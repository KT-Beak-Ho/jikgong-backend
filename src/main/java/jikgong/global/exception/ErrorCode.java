package jikgong.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보가 없습니다."),
    MEMBER_PHONE_EXIST(HttpStatus.CONFLICT, "이미 사용 핸드폰 입니다."),
    MEMBER_INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    REQUEST_INVALID(HttpStatus.BAD_REQUEST, "valid 옵션에 맞지 않는 형식입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 access token 입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 refresh token 입니다."),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.FORBIDDEN, "유효하지 않은 refresh token 입니다. 다시 로그인하세요."),
    FCM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 알림 전송 중 에러가 발생했습니다."),
    FCM_FIREBASE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보에 FCM 토큰이 없습니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "위치 정보가 없습니다."),
    WAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "임금 지급 내역 정보가 없습니다.");

    private final HttpStatus status;
    private final String errorMessage;

}
