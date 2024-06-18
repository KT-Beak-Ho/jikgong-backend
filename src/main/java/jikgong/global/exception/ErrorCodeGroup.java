package jikgong.global.exception;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorCodeGroup {

    private String key;
    private List<ErrorCodeResponse> errorCodes;

    @Getter
    @AllArgsConstructor
    public static class ErrorCodeResponse {

        private HttpStatus status;
        private String code;
        private String errorMessage;
    }
}