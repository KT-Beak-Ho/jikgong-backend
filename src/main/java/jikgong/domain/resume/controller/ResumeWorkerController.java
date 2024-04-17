package jikgong.domain.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.resume.dtos.worker.ResumeSaveRequest;
import jikgong.domain.resume.service.ResumeWorkerService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResumeWorkerController {
    private final ResumeWorkerService resumeWorkerService;


    @Operation(summary = "노동자: 이력서 등록 (헤드헌팅 노출)")
    @PostMapping("/api/resume")
    public ResponseEntity<Response> saveResume(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                               @RequestBody ResumeSaveRequest request) {
        resumeWorkerService.saveResume(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("이력서 등록 완료"));
    }
}
