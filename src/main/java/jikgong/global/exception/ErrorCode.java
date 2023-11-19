package jikgong.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NO_CONTENT, "회원 정보가 없습니다."),
    REQUEST_INVALID(HttpStatus.BAD_REQUEST, "valid 옵션에 맞지 않는 형식입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 access token 입니다.");

    private final HttpStatus status;
    private final String errorMessage;

}
