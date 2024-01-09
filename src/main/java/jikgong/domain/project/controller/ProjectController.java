package jikgong.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.project.dtos.ProjectSaveRequest;
import jikgong.domain.project.service.ProjectService;
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
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성")
    @PostMapping("/api/project")
    public ResponseEntity<Response> saveProject(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody ProjectSaveRequest request) {
        Long projectId = projectService.saveProject(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("프로젝트 생성 완료"));
    }
}
