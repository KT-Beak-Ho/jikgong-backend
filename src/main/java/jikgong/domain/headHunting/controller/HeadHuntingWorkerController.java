package jikgong.domain.headHunting.controller;


import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.headHunting.dtos.HeadHuntingSaveRequest;
import jikgong.domain.headHunting.service.HeadHuntingWorkerService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HeadHuntingWorkerController {
    private final HeadHuntingWorkerService headHuntingWorkerService;

    @Operation(summary = "노동자: 헤드헌팅 등록")
    @PostMapping("/api/head-hunting")
    public ResponseEntity<Response> saveHeadHunting(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @RequestBody HeadHuntingSaveRequest request) {
        headHuntingWorkerService.saveHeadHunting(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("노동자: 헤드헌팅 등록 완료"));
    }
}
