package jikgong.domain.resume.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.domain.resume.dtos.worker.CareerDetailRequest;
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
}
