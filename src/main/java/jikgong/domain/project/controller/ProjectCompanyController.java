package jikgong.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.project.dto.*;
import jikgong.domain.project.entity.ProjectStatus;
import jikgong.domain.project.service.ProjectService;
import jikgong.global.annotation.CompanyRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[사업자] 프로젝트")
@RestController
@RequiredArgsConstructor
@Slf4j
@CompanyRoleRequired // 권한 설정
public class ProjectCompanyController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성")
    @PostMapping("/api/project")
    public ResponseEntity<Response> saveProject(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody ProjectSaveRequest request) {
        Long companyId = principalDetails.getMember().getId();

        Long projectId = projectService.saveProject(companyId, request);

        ProjectDetailResponse projectDetailResponse = projectService.findProjectDetail(companyId, projectId);

        ProjectSaveResponse projectSaveResponse = ProjectSaveResponse.builder()
                .projectId(projectId)
                .project(projectDetailResponse)
                .build();
        return ResponseEntity.ok(new Response(projectSaveResponse, "프로젝트 생성 완료"));
    }

    @Operation(summary = "프로젝트 목록 조회")
    @GetMapping("/api/project/list")
    public ResponseEntity<Response> getProjects(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestParam(required = false, name = "projectStatus") ProjectStatus projectStatus,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("startDate")));

        ProjectListResponse projectListResponse = projectService.findProjects(
            principalDetails.getMember().getId(), projectStatus, pageable);

        if(projectStatus == null) {
            return ResponseEntity.ok(new Response(projectListResponse, "프로젝트 목록 조회"));

        }
        else {
            return ResponseEntity.ok(new Response(projectListResponse, projectStatus.getDescription() + " 프로젝트 조회"));
        }
    }

    @Operation(summary = "프로젝트 상세 조회")
    @GetMapping("/api/project/{projectId}")
    public ResponseEntity<Response> getProjectDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId) {
        ProjectDetailResponse projectDetailResponse = projectService.findProjectDetail(principalDetails.getMember().getId(),
            projectId);
        return ResponseEntity.ok(new Response(projectDetailResponse, "프로젝트 상세 조회"));
    }

    @Operation(summary = "프로젝트 수정")
    @PutMapping("/api/project/{projectId}")
    public ResponseEntity<Response> updateProject(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId, @RequestBody ProjectUpdateRequest request) {
        Long companyId = principalDetails.getMember().getId();

        projectService.updateProject(companyId, projectId, request);

        ProjectDetailResponse project = projectService.findProjectDetail(companyId, projectId);

        return ResponseEntity.ok(new Response(project, "프로젝트 조회"));
    }



    @Operation(summary = "프로젝트 삭제", description = "관련된 모집 공고가 전부 삭제된 후 삭제 가능")
    @DeleteMapping("/api/project/{projectId}")
    public ResponseEntity<Response> deleteProject(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId) {
        projectService.deleteProject(principalDetails.getMember().getId(), projectId);
        return ResponseEntity.ok(new Response("프로젝트 삭제 완료"));
    }
}
