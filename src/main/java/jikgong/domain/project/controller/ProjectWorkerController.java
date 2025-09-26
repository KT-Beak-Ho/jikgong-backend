package jikgong.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.project.dto.ProjectDetailForSearchResponse;
import jikgong.domain.project.dto.ProjectListSearchResponse;
import jikgong.domain.project.service.ProjectService;
import jikgong.global.annotation.CompanyRoleRequired;
import jikgong.global.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "[노동자] 프로젝트")
@RestController
@RequiredArgsConstructor
@Slf4j
@CompanyRoleRequired
public class ProjectWorkerController {
    private final ProjectService projectService;

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
}
