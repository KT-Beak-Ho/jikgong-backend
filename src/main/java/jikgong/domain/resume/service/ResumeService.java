package jikgong.domain.resume.service;

import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.resume.dtos.ResumeSaveRequest;
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
public class ResumeService {
    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;

    public void saveResume(Long memberId, ResumeSaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Resume resume = Resume.createEntity(request, member);

        resumeRepository.save(resume);

        List<Tech> techList = request.getTechList();
        List<Skill> skillList = new ArrayList<>();
        for (Tech tech : techList) {
            skillList.add(Skill.createEntity(tech, resume));
        }
        skillRepository.saveAll(skillList);
    }
}
