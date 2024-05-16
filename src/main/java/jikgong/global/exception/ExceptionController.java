package jikgong.global.exception;

import jikgong.global.dto.Response;
import jikgong.global.slack.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionController {

    private final SlackService slackService;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {
        log.error("핸들링한 에러 발생");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(e.getStatus())
                .code(e.getStatus().value())
                .errorMessage(e.getErrorMessage())
                .build();
        return ResponseEntity.status(e.getStatus()).body(new Response<>(errorResponse, "커스텀 예외 반환"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> processValidationError(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }
        log.error("valid 검사 에러 발생: " + builder);
        CustomException e = new CustomException(ErrorCode.REQUEST_INVALID);
        return ResponseEntity.status(e.getStatus()).body(new Response(e.getMessage(), builder.toString()));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> unHandledException(Exception e) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String currentTime = sdf.format(new Date());
//
//        // 에러 발생 위치 추출
//        StackTraceElement[] stackTrace = e.getStackTrace();
//        String errorLocation = "";
//        if (stackTrace.length > 0) {
//            StackTraceElement element = stackTrace[0];
//            errorLocation = element.getClassName() + " at line " + element.getLineNumber();
//        }
//
//        // 로그에 에러 정보 기록
//        log.error("핸들링하지 않은 에러 발생");
//        log.error("발생 시각" + currentTime);
//        log.error("에러 위치: " + errorLocation);
//        log.error("에러 내용: " + e.getMessage());
//
//
//        // Slack 메시지와 데이터에 추가 정보를 포함
//        HashMap<String, String> data = new HashMap<>();
//        data.put("에러 내용", e.getMessage());
//        data.put("발생 시각", currentTime);
//        data.put("에러 위치", errorLocation);
//        slackService.sendMessage("Unhandled exception occurred", data);
//
//
//        return ResponseEntity.badRequest().body(new Response(e.getMessage()));
//    }
}
