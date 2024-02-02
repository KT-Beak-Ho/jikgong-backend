package jikgong.domain.scrap.service;

import jikgong.domain.jobPost.dtos.worker.JobPostListResponse;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.scrap.entity.Scrap;
import jikgong.domain.scrap.repository.ScrapRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScrapService {
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;
    private final ScrapRepository scrapRepository;
    private final LocationRepository locationRepository;

    public Boolean saveScrap(Long memberId, Long jobPostId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        // 이미 스크랩한 경우 취소
        // 그렇지 않다면 스크랩
        Optional<Scrap> findScrap = scrapRepository.findByMemberAndJobPost(member.getId(), jobPost.getId());
        if (findScrap.isPresent()) {
            scrapRepository.delete(findScrap.get());
            log.info("스크랩 취소");
            return false;
        } else {
            Scrap scrap = Scrap.createEntity(member, jobPost);
            scrapRepository.save(scrap);
            log.info("스크랩 저장");
            return true;
        }
    }


    public Page<JobPostListResponse> findScrapJobPostList(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Location location = locationRepository.findMainLocationByMemberId(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOCATION_NOT_FOUND));

        Page<JobPostListResponse> jobPostListResponsePage = scrapRepository.findByMember(member, location, pageable);

        return jobPostListResponsePage;
    }
}
