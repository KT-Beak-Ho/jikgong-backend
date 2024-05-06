package jikgong.domain.jobPost.service;

import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.jobPost.dtos.worker.JobPostDetailResponse;
import jikgong.domain.jobPost.dtos.worker.JobPostListResponse;
import jikgong.domain.jobPost.dtos.worker.JobPostMapResponse;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.SortType;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.offerWorkDate.repository.OfferWorkDateRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobPostWorkerService {

    private final JobPostRepository jobPostRepository;
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;
    private final ApplyRepository applyRepository;
    private final OfferWorkDateRepository offerWorkDateRepository;

    /**
     * 메인 페이지 조회
     * 필터: 직종, 날짜, 스크랩, 식사 제공, 주차 여부, 정렬 기준
     */
    @Transactional(readOnly = true)
    public Page<JobPostListResponse> getMainPage(Long workerId, List<Tech> techList, List<LocalDate> workDateList, Boolean scrap, Boolean meal, Park park, SortType sortType, Pageable pageable) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Location location = locationRepository.findMainLocationByMemberId(worker.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOCATION_NOT_FOUND));

        // querydsl
        Page<JobPostListResponse> jobPostPage = jobPostRepository.getMainPage(worker.getId(), techList, workDateList, scrap, meal, park, location, sortType, pageable);

        return jobPostPage;
    }

    /**
     * 모집 공고 상세 화면
     */
    @Transactional(readOnly = true)
    public JobPostDetailResponse getJobPostDetail(Long workerId, Long jobPostId) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        JobPost jobPost = jobPostRepository.findByIdWithMember(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        Location location = locationRepository.findMainLocationByMemberId(worker.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOCATION_NOT_FOUND));

        return JobPostDetailResponse.from(jobPost, location);
    }

    /**
     * 모집 공고 지도에서 조회
     */
    @Transactional(readOnly = true)
    public List<JobPostMapResponse> findJobPostsOnMap(Long workerId, Float northEastLat, Float northEastLng, Float southWestLat, Float southWestLng, List<Tech> techList, List<LocalDate> dateList, Boolean scrap) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return jobPostRepository.findJobPostOnMap(worker.getId(), northEastLat, northEastLng, southWestLat, southWestLng, techList, dateList, scrap).stream()
                .map(JobPostMapResponse::from)
                .collect(Collectors.toList());
    }
}
