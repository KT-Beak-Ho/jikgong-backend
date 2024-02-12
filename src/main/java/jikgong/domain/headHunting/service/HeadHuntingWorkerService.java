package jikgong.domain.headHunting.service;


import jikgong.domain.headHunting.dtos.HeadHuntingSaveRequest;
import jikgong.domain.headHunting.entity.HeadHunting;
import jikgong.domain.headHunting.repository.HeadHuntingRepository;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.project.repository.ProjectRepository;
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
public class HeadHuntingWorkerService {
    private final MemberRepository memberRepository;
    private final HeadHuntingRepository headHuntingRepository;
    private final SkillRepository skillRepository;

    public void saveHeadHunting(Long memberId, HeadHuntingSaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        HeadHunting headHunting = HeadHunting.createEntity(request, member);

        headHuntingRepository.save(headHunting);

        List<Tech> techList = request.getTechList();
        List<Skill> skillList = new ArrayList<>();
        for (Tech tech : techList) {
            skillList.add(Skill.createEntity(tech, headHunting));
        }
        skillRepository.saveAll(skillList);
    }
}
