//package jikgong.domain.etc.searchLog2;
//
//import jikgong.global.security.principal.PrincipalDetails;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//public class SearchLog2Controller {
//    private final SearchLog2Service searchLogService;
//
//    @PostMapping("/api/searchLog2")
//    public String saveSearchLog2(@AuthenticationPrincipal PrincipalDetails principalDetails,
//                                 @RequestBody SearchLogRequest request) {
//        searchLogService.saveSearchLog(principalDetails.getMember().getId(), request);
//        return "완료";
//    }
//}
