package jikgong.domain.scrap.service;

import java.util.Optional;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.jobpost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.scrap.entity.Scrap;
import jikgong.domain.scrap.repository.ScrapRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScrapService {

    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;
    private final ScrapRepository scrapRepository;

    /**
     * 스크랩 저장
     * 이미 스크랩 된 공고 글 이라면 취소
     * 그렇지 않다면 저장
     */
    public Boolean processScrap(Long workerId, Long jobPostId) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        JobPost jobPost = jobPostRepository.findById(jobPostId)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));

        // 이미 스크랩 한 경우 취소
        // 그렇지 않다면 스크랩
        Optional<Scrap> findScrap = scrapRepository.findByMemberAndJobPost(worker.getId(), jobPost.getId());
        if (findScrap.isPresent()) {
            scrapRepository.delete(findScrap.get());
            log.info("스크랩 취소");
            return false;
        } else {
            Scrap scrap = Scrap.createEntity(worker, jobPost);
            scrapRepository.save(scrap);
            log.info("스크랩 저장");
            return true;
        }
    }
}
