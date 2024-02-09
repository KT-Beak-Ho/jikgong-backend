package jikgong.domain.headHunting.service;

import jikgong.domain.headHunting.entity.SortType;
import jikgong.domain.headHunting.repository.HeadHuntingRepository;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HeadHuntingService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final HeadHuntingRepository headHuntingRepository;
    private final LocationRepository locationRepository;

    public void findHeadHuntingList(Long memberId, Long projectId, Tech tech, Float bound, SortType sortType, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        headHuntingRepository.findHeadHuntingMemberList(project.getAddress(), tech, bound, sortType, pageable);
    }
}
