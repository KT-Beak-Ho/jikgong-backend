package jikgong.domain.project.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.project.dtos.ProjectDetailResponse;
import jikgong.domain.project.dtos.ProjectListResponse;
import jikgong.domain.project.dtos.ProjectSaveRequest;
import jikgong.domain.project.dtos.ProjectUpdateRequest;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.entity.ProjectStatus;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.domain.workDate.entity.WorkDate;
import jikgong.domain.workDate.repository.WorkDateRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final WorkDateRepository workDateRepository;

    /**
     * 프로젝트 등록
     */
    public Long saveProject(Long companyId, ProjectSaveRequest request) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = Project.createEntity(request, company);

        return projectRepository.save(project).getId();
    }

    /**
     * 프로젝트 목록 조회
     * 필터: 완료됨, 진행 중, 예정
     */
    @Transactional(readOnly = true)
    public List<ProjectListResponse> findProjects(Long companyId, ProjectStatus projectStatus) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDate now = LocalDate.now();
        List<Project> projectList = new ArrayList<>();

        if (projectStatus == ProjectStatus.COMPLETED) {
            projectList = projectRepository.findCompletedProject(company.getId(), now);
        }
        else if (projectStatus == ProjectStatus.IN_PROGRESS) {
            projectList = projectRepository.findInProgressProject(company.getId(), now);
        }
        else if (projectStatus == ProjectStatus.PLANNED) {
            projectList = projectRepository.findPlannedProject(company.getId(), now);
        }

        List<ProjectListResponse> projectListResponseList = projectList.stream()
                .map(ProjectListResponse::from)
                .collect(Collectors.toList());

        return projectListResponseList;
    }

    /**
     * 프로젝트 수정
     */
    public void updateProject(Long companyId, ProjectUpdateRequest request) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findByIdAndMember(company.getId(), request.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        project.updateProject(request);
    }

    /**
     * 기업 검색
     * 기업 선택 시 등록한 프로젝트 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<ProjectListResponse> findProjectAtSearch(Long companyId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 준공일이 지나지 않은 project 조회
        return projectRepository.findNotCompletedProject(company.getId(), LocalDate.now()).stream()
                .map(ProjectListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 기업 검색
     * 기업 검색 시 기업의 프로젝트 상세 조회
     */
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetail(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        // 프로젝트에 해당하는 workDate 조회
<<<<<<< Updated upstream
        List<WorkDate> workDateList = workDateRepository.findByProject(project.getId(), LocalDate.now());
=======
        List<JobPostListResponse> jobPostListResponseList = workDateRepository.findByProject(project.getId()).stream()
                .map(JobPostListResponse::from)
                .collect(Collectors.toList());
>>>>>>> Stashed changes

        return ProjectDetailResponse.from(project, workDateList);
    }
}
