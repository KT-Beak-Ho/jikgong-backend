package jikgong.domain.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.resume.dto.worker.ResumeDetailResponse;
import jikgong.domain.resume.dto.worker.ResumeSaveRequest;
import jikgong.domain.resume.service.ResumeWorkerService;
import jikgong.global.annotation.WorkerRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@WorkerRoleRequired
public class ResumeWorkerController {

    private final ResumeWorkerService resumeWorkerService;

    @Operation(summary = "노동자: 이력서 등록 (헤드헌팅 노출)")
    @PostMapping("/api/resume/worker")
    public ResponseEntity<Response> saveResume(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody ResumeSaveRequest request) {
        resumeWorkerService.saveResume(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("이력서 등록 완료"));
    }

    @Operation(summary = "노동자: 등록한 이력서 조회")
    @GetMapping("/api/resume/worker")
    public ResponseEntity<Response> findResume(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        ResumeDetailResponse resumeDetailResponse = resumeWorkerService.findResume(
            principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(resumeDetailResponse, "등록한 이력서 조회 완료"));
    }
}
