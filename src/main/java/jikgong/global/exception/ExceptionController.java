package jikgong.global.exception;

import jakarta.persistence.OptimisticLockException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import jikgong.global.common.Response;
import jikgong.global.slack.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionController {

    private final SlackService slackService;

    @ExceptionHandler(JikgongException.class)
    public ResponseEntity<?> handleCustomException(JikgongException e) {
        logError(e);
        ErrorResponse errorResponse = buildErrorResponse(e.getStatus(), e.getErrorCode(), e.getErrorMessage());
        return ResponseEntity.status(e.getStatus()).body(new Response<>(errorResponse, "커스텀 예외 반환"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> processValidationError(MethodArgumentNotValidException exception) {
        String errorMessage = buildValidationErrorMessage(exception.getBindingResult());
        log.error("valid 검사 에러 발생: " + errorMessage);

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, "VALID-001", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(errorResponse, "커스텀 예외 반환"));
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<?> handleOptimisticLockException(OptimisticLockException e) {
        logError(e);
        JikgongException exception = new JikgongException(ErrorCode.CONCURRENCY_FAILURE);
        ErrorResponse errorResponse = buildErrorResponse(exception.getStatus(), exception.getErrorCode(),
            exception.getErrorMessage());
        return ResponseEntity.status(exception.getStatus()).body(new Response<>(errorResponse, "낙관적 락 예외 반환"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnhandledException(Exception e) {
        logError(e);
        sendSlackNotification(e);
        return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
    }

    private void logError(Exception e) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        StackTraceElement[] stackTrace = e.getStackTrace();
        String errorLocation = "";
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            errorLocation = element.getClassName() + " at line " + element.getLineNumber();
        }

        log.error("에러 발생 시각: " + currentTime);
        log.error("에러 위치: " + errorLocation);
        log.error("에러 내용: " + e.getMessage());
    }

    private void sendSlackNotification(Exception e) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        StackTraceElement[] stackTrace = e.getStackTrace();
        String errorLocation = "";
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            errorLocation = element.getClassName() + " at line " + element.getLineNumber();
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("에러 내용", e.getMessage());
        data.put("발생 시각", currentTime);
        data.put("에러 위치", errorLocation);
        slackService.sendMessage("Unhandled exception occurred", data);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String code, String errorMessage) {
        return ErrorResponse.builder()
            .status(status)
            .code(code)
            .errorMessage(errorMessage)
            .build();
    }

    private String buildValidationErrorMessage(BindingResult bindingResult) {
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("] ");
        }
        return builder.toString();
    }
}
