package jikgong.domain.resume.service;

import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.offer.entity.SortType;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.domain.resume.dtos.CareerDetailRequest;
import jikgong.domain.resume.dtos.ResumeListResponse;
import jikgong.domain.resume.dtos.ResumeSaveRequest;
import jikgong.domain.resume.entity.Resume;
import jikgong.domain.resume.repository.ResumeRepository;
import jikgong.domain.skill.entity.Skill;
import jikgong.domain.skill.repository.SkillRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ResumeService {
    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;

    /**
     * 노동자
     * 이력서 등록
     */
    public void saveResume(Long workerId, ResumeSaveRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Resume resume = Resume.createEntity(request, worker);

        resumeRepository.save(resume);

        List<CareerDetailRequest> careerDetailRequestList = request.getCareerDetailRequestList();
        List<Skill> skillList = new ArrayList<>();
        for (CareerDetailRequest careerDetailRequest : careerDetailRequestList) {
            skillList.add(Skill.createEntity(careerDetailRequest, resume));
        }
        skillRepository.saveAll(skillList);
    }

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
}
