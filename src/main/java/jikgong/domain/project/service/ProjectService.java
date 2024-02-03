package jikgong.domain.project.service;

import jikgong.domain.jobPost.entity.JobPostStatus;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.project.dtos.ProjectListResponse;
import jikgong.domain.project.dtos.ProjectSaveRequest;
import jikgong.domain.project.dtos.ProjectUpdateRequest;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.entity.ProjectStatus;
import jikgong.domain.project.repository.ProjectRepository;
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

    public Long saveProject(Long memberId, ProjectSaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = Project.createEntity(request, member);

        return projectRepository.save(project).getId();
    }


    @Transactional(readOnly = true)
    public List<ProjectListResponse> findProjects(Long memberId, ProjectStatus projectStatus) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDate now = LocalDate.now();
        List<Project> projectList = new ArrayList<>();

        if (projectStatus == ProjectStatus.COMPLETED) {
            projectList = projectRepository.findCompletedProject(member.getId(), now);
        }
        else if (projectStatus == ProjectStatus.IN_PROGRESS) {
            projectList = projectRepository.findInProgressProject(member.getId(), now);
        }
        else if (projectStatus == ProjectStatus.PLANNED) {
            projectList = projectRepository.findPlannedProject(member.getId(), now);
        }

        List<ProjectListResponse> projectListResponseList = projectList.stream()
                .map(ProjectListResponse::from)
                .collect(Collectors.toList());

        return projectListResponseList;
    }

    public void updateProject(Long memberId, ProjectUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        project.updateProject(request);
    }
}
