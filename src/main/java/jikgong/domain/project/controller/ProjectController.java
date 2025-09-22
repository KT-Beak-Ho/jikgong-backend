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
public class ProjectController {

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

    @Operation(summary = "프로젝트 리스트 조회")
    @GetMapping("/api/project/list")
    public ResponseEntity<Response> getProjects(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestParam(required = false, name = "projectStatus") ProjectStatus projectStatus,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("startDate")));

        ProjectListResponse projectListResponse = projectService.findProjects(
            principalDetails.getMember().getId(), projectStatus, pageable);

        if(projectStatus == null) {
            return ResponseEntity.ok(new Response(projectListResponse, "모든 프로젝트 조회"));

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

    @Operation(summary = "기업 검색: 프로젝트 리스트 조회")
    @GetMapping("/api/project/list/{companyId}")
    public ResponseEntity<Response> findProjectsForSearch(@PathVariable(name = "companyId") Long companyId) {
        List<ProjectListSearchResponse> projectListSearchResponse = projectService.findProjectsForSearch(companyId);
        return ResponseEntity.ok(new Response(projectListSearchResponse, "기업이 등록한 프로젝트 조회 완료"));
    }

    @Operation(summary = "기업 검색: 프로젝트 상세 정보", description = "현장정보, 건설 시공정보, 현장에 등록된 일자리 조회")
    @GetMapping("/api/project/search/{projectId}")
    public ResponseEntity<Response> getProjectDetailForSearch(@PathVariable(name = "projectId") Long projectId) {
        ProjectDetailForSearchResponse projectDetailForSearchResponse = projectService.getProjectDetailForSearch(projectId);
        return ResponseEntity.ok(new Response(projectDetailForSearchResponse, "기업이 등록한 프로젝트 상세 조회 완료"));
    }

    @Operation(summary = "프로젝트 삭제", description = "관련된 모집 공고가 전부 삭제된 후 삭제 가능")
    @DeleteMapping("/api/project/{projectId}")
    public ResponseEntity<Response> deleteProject(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId) {
        projectService.deleteProject(principalDetails.getMember().getId(), projectId);
        return ResponseEntity.ok(new Response("프로젝트 삭제 완료"));
    }
}
