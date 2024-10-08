package jikgong.domain.project.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.jobpost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.project.dto.ProjectDetailResponse;
import jikgong.domain.project.dto.ProjectInfoResponse;
import jikgong.domain.project.dto.ProjectListResponse;
import jikgong.domain.project.dto.ProjectSaveRequest;
import jikgong.domain.project.dto.ProjectUpdateRequest;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.entity.ProjectStatus;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.domain.workdate.entity.WorkDate;
import jikgong.domain.workdate.repository.WorkDateRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final WorkDateRepository workDateRepository;
    private final JobPostRepository jobPostRepository;

    /**
     * 프로젝트 등록
     */
    public Long saveProject(Long companyId, ProjectSaveRequest request) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = Project.createEntity(request, company);

        return projectRepository.save(project).getId();
    }

    /**
     * 프로젝트 목록 조회
     * 필터: 완료됨, 진행 중, 예정
     */
    @Transactional(readOnly = true)
    public Page<ProjectListResponse> findProjects(Long companyId, ProjectStatus projectStatus, Pageable pageable) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDate now = LocalDate.now();
        Page<Project> projectPage = Page.empty();

        if (projectStatus == ProjectStatus.COMPLETED) {
            projectPage = projectRepository.findCompletedProject(company.getId(), now, pageable);
        } else if (projectStatus == ProjectStatus.IN_PROGRESS) {
            projectPage = projectRepository.findInProgressProject(company.getId(), now, pageable);
        } else if (projectStatus == ProjectStatus.PLANNED) {
            projectPage = projectRepository.findPlannedProject(company.getId(), now, pageable);
        }

        List<ProjectListResponse> projectListResponseList = projectPage.getContent().stream()
            .map(ProjectListResponse::from)
            .collect(Collectors.toList());

        return new PageImpl<>(projectListResponseList, pageable, projectPage.getTotalElements());
    }

    /**
     * 프로젝트 수정
     */
    public void updateProject(Long companyId, ProjectUpdateRequest request) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findByIdAndMember(company.getId(), request.getProjectId())
            .orElseThrow(() -> new JikgongException(ErrorCode.PROJECT_NOT_FOUND));

        project.updateProject(request);
    }

    /**
     * 기업 검색
     * 기업 선택 시 등록한 프로젝트 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<ProjectListResponse> findProjectAtSearch(Long companyId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

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
    public ProjectDetailResponse getProjectDetailForSearch(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new JikgongException(ErrorCode.PROJECT_NOT_FOUND));

        // 프로젝트에 해당하는 workDate 조회
        List<WorkDate> workDateList = workDateRepository.findByProject(project.getId());

        return ProjectDetailResponse.from(project, workDateList);
    }

    /**
     * 프로젝트 수정에 필요한 정보 반환
     */
    @Transactional(readOnly = true)
    public ProjectInfoResponse findProjectInfo(Long companyId, Long projectId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new JikgongException(ErrorCode.PROJECT_NOT_FOUND));

        return ProjectInfoResponse.from(project);
    }

    public void deleteProject(Long companyId, Long projectId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findByIdAndMember(company.getId(), projectId)
            .orElseThrow(() -> new JikgongException(ErrorCode.PROJECT_NOT_FOUND));

        // 프로젝트에 등록된 모집 공고가 있을 시 삭제 실패
        if (jobPostRepository.existsNotDeletedJobPost(project.getId())) {
            throw new JikgongException(ErrorCode.PROJECT_DELETE_FAIL);
        }

        // 조건이 충족되면 논리 삭제 실행
        projectRepository.delete(project);
    }
}
