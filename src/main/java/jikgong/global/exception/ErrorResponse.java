package jikgong.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ErrorResponse {
    private HttpStatus status;
    private int code;
    private String errorMessage;
}
