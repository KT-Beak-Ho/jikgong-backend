package jikgong.domain.resume.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.resume.dtos.worker.ResumeDetailResponse;
import jikgong.domain.resume.dtos.worker.SkillDetailRequest;
import jikgong.domain.resume.dtos.worker.ResumeSaveRequest;
import jikgong.domain.resume.entity.Resume;
import jikgong.domain.resume.repository.ResumeRepository;
import jikgong.domain.skill.entity.Skill;
import jikgong.domain.skill.repository.SkillRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ResumeWorkerService {
    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;

    /**
     * 이력서 등록
     */
    public void saveResume(Long workerId, ResumeSaveRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Resume resume = Resume.createEntity(request, worker);

        resumeRepository.save(resume);

        List<Skill> skillList = new ArrayList<>();
        for (SkillDetailRequest skillDetailRequest : request.getSkillDetailRequestList()) {
            skillList.add(Skill.createEntity(skillDetailRequest, resume));
        }
        skillRepository.saveAll(skillList);
    }

    /**
     * 이력서 조회
     */
    public ResumeDetailResponse findResume(Long workerId) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Resume resume = resumeRepository.findByMember(worker.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        return ResumeDetailResponse.from(resume);
    }
}
