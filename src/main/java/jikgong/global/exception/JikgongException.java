package jikgong.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JikgongException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;

    // ErrorCode 생성자
    public JikgongException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getErrorMessage();
    }

    public JikgongException(HttpStatus status, String errorCode, String errorMessage) {
        super(errorMessage);
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
