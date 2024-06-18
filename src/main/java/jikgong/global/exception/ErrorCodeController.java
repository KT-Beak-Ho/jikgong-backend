package jikgong.global.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jikgong.global.exception.ErrorCodeGroup.ErrorCodeResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorCodeController {

    /**
     * 커스텀 에러 코드 문서화를 위한 컨트롤러
     */
    @GetMapping("/error-codes")
    public String getAllErrorCodes(Model model) {
        Map<String, List<ErrorCodeResponse>> groupedErrorCodes = Stream.of(ErrorCode.values())
            .map(errorCode -> new ErrorCodeResponse(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getErrorMessage()))
            .collect(Collectors.groupingBy(errorCodeResponse -> errorCodeResponse.getCode().split("-")[0]));

        List<ErrorCodeGroup> errorCodeGroups = groupedErrorCodes.entrySet().stream()
            .map(entry -> new ErrorCodeGroup(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        model.addAttribute("errorCodeGroups", errorCodeGroups);

        return "errorCodes";
    }
}
