package jikgong.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.project.dtos.ProjectListResponse;
import jikgong.domain.project.dtos.ProjectSaveRequest;
import jikgong.domain.project.dtos.ProjectUpdateRequest;
import jikgong.domain.project.entity.ProjectStatus;
import jikgong.domain.project.service.ProjectService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "프로젝트 리스트 조회")
    @GetMapping("/api/projects")
    public ResponseEntity<Response> findProjects(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestParam("projectStatus")ProjectStatus projectStatus) {
        List<ProjectListResponse> projectListResponseList = projectService.findProjects(principalDetails.getMember().getId(), projectStatus);
        return ResponseEntity.ok(new Response(projectListResponseList, "프로젝트 중 " + projectStatus.getDescription() + " 프로젝트 조회"));
    }

    @Operation(summary = "프로젝트 수정")
    @PutMapping("/api/project")
    public ResponseEntity<Response> updateProject(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestBody ProjectUpdateRequest request) {
        projectService.updateProject(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("프로젝트 조회"));
    }
}
