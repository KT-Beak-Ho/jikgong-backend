package jikgong.global.exception;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorCodeView {

    private Map<String, String> errorCodes;
}