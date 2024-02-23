package jikgong.domain.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.resume.dtos.ResumeSaveRequest;
import jikgong.domain.resume.service.ResumeService;
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
public class ResumeController {
    private final ResumeService resumeService;

    @Operation(summary = "노동자: 이력서 등록 (헤드헌팅 노출)")
    @PostMapping("/api/resume")
    public ResponseEntity<Response> saveResume(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @RequestBody ResumeSaveRequest request) {
        resumeService.saveResume(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("이력서 등록 완료"));
    }
}
