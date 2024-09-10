package jikgong.domain.resume.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.offer.entity.SortType;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.domain.resume.dto.company.ResumeDetailResponse;
import jikgong.domain.resume.dto.company.ResumeListResponse;
import jikgong.domain.resume.entity.Resume;
import jikgong.domain.resume.repository.ResumeRepository;
import jikgong.domain.workexperience.entity.Tech;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ResumeCompanyService {

    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final ProjectRepository projectRepository;


    /**
     * 기업
     * 일자리 제안 시 이력서 조회
     */
    @Transactional(readOnly = true)
    public Page<ResumeListResponse> findResumeList(Long companyId, Long projectId, Tech tech, Float bound,
        SortType sortType, Pageable pageable) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findByIdAndMember(company.getId(), projectId)
            .orElseThrow(() -> new JikgongException(ErrorCode.PROJECT_NOT_FOUND));

        // querydsl
        return resumeRepository.findHeadHuntingMemberList(project.getAddress(), tech, bound, sortType, pageable);
    }

    /**
     * 기업
     * 제안 시 노동자 상세 정보 조회
     */
    @Transactional(readOnly = true)
    public ResumeDetailResponse findResumeDetail(Long companyId, Long resumeId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Resume resume = resumeRepository.findByIdWithMember(resumeId)
            .orElseThrow(() -> new JikgongException(ErrorCode.RESUME_NOT_FOUND));

        return ResumeDetailResponse.from(resume);
    }
}
