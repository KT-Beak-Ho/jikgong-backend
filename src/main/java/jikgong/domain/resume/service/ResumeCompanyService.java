package jikgong.domain.resume.service;

import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.resume.dtos.company.ResumeDetailResponse;
import jikgong.domain.offer.entity.SortType;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.domain.resume.dtos.company.ResumeListResponse;
import jikgong.domain.resume.entity.Resume;
import jikgong.domain.resume.repository.ResumeRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
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
    public Page<ResumeListResponse> findResumeList(Long companyId, Long projectId, Tech tech, Float bound, SortType sortType, Pageable pageable) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findByIdAndMember(company.getId(), projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

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
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Resume resume = resumeRepository.findByIdWithMember(resumeId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

//        // 이미 확정된 apply 월별 조회
//        List<Apply> findCantWorkDate = applyRepository.findCantWorkDate(
//                resume.getMember().getId(),
//                TimeTransfer.getFirstDayOfMonth(selectMonth),
//                TimeTransfer.getLastDayOfMonth(selectMonth));

        return ResumeDetailResponse.from(resume);
    }
}
