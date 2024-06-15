package jikgong.domain.jobpost.service;

import jikgong.domain.jobpost.dto.worker.JobPostDetailResponse;
import jikgong.domain.jobpost.dto.worker.JobPostListResponse;
import jikgong.domain.jobpost.dto.worker.JobPostMapResponse;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Park;
import jikgong.domain.jobpost.entity.SortType;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.jobpost.repository.JobPostRepository;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.scrap.repository.ScrapRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobPostWorkerService {

    private final JobPostRepository jobPostRepository;
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;
    private final ScrapRepository scrapRepository;

    /**
     * 메인 페이지 조회 (회원)
     * 필터: 직종, 날짜, 스크랩, 식사 제공, 주차 여부, 정렬 기준
     */
    @Transactional(readOnly = true)
    public Page<JobPostListResponse> getMainPageForMember(Long memberId, List<Tech> techList, List<LocalDate> workDateList, Boolean scrap, Boolean meal, Park park, SortType sortType, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Location location = locationRepository.findMainLocationByMemberId(member.getId())
                .orElse(null);

        // querydsl
        Page<JobPost> jobPostPage = jobPostRepository.getMainPage(memberId, techList, workDateList, scrap, meal, park, location, sortType, pageable);
        List<JobPostListResponse> jobPostListResponseList = jobPostPage.getContent().stream()
                .map(jobPost -> JobPostListResponse.from(jobPost, location))
                .collect(Collectors.toList());

        // scrap 했는지 정보 설정
        // worker의 scrap 리스트
        Set<Long> scrapJobPostIdSet = scrapRepository.findScrapJobPostIdByMember(member.getId());
        for (JobPostListResponse jobPostListResponse : jobPostListResponseList) {
            if (scrapJobPostIdSet.contains(jobPostListResponse.getJobPostId())) {
                jobPostListResponse.setScrap(true);
            } else {
                jobPostListResponse.setScrap(false);
            }
        }

        return new PageImpl<>(jobPostListResponseList, pageable, jobPostPage.getTotalElements());
    }

    /**
     * 메인 페이지 조회 비회원
     * 필터: 직종, 날짜, 스크랩, 식사 제공, 주차 여부, 정렬 기준
     */
    @Transactional(readOnly = true)
    public Page<JobPostListResponse> getMainPageForNonMember(List<Tech> techList, List<LocalDate> workDateList, Boolean scrap, Boolean meal, Park park, SortType sortType, Pageable pageable) {

        // querydsl
        Page<JobPost> jobPostPage = jobPostRepository.getMainPage(null, techList, workDateList, scrap, meal, park, null, sortType, pageable);
        List<JobPostListResponse> jobPostListResponseList = jobPostPage.getContent().stream()
                .map(jobPost -> JobPostListResponse.from(jobPost, null))
                .collect(Collectors.toList());

        return new PageImpl<>(jobPostListResponseList, pageable, jobPostPage.getTotalElements());
    }

    /**
     * 모집 공고 상세 화면 (회원)
     */
    @Transactional(readOnly = true)
    public JobPostDetailResponse getJobPostDetailForMember(Long workerId, Long jobPostId) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        JobPost jobPost = jobPostRepository.findByIdWithMember(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        Location location = locationRepository.findMainLocationByMemberId(worker.getId())
                .orElse(null);

        return JobPostDetailResponse.from(jobPost, location);
    }

    /**
     * 모집 공고 상세 화면 (비회원)
     */
    @Transactional(readOnly = true)
    public JobPostDetailResponse getJobPostDetailForNonMember(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findByIdWithMember(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        return JobPostDetailResponse.from(jobPost, null);
    }

    /**
     * 모집 공고 지도에서 조회 (회원)
     */
    @Transactional(readOnly = true)
    public List<JobPostMapResponse> findJobPostsOnMapForMember(Long workerId, Float northEastLat, Float northEastLng, Float southWestLat, Float southWestLng, List<Tech> techList, List<LocalDate> dateList, Boolean scrap) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return jobPostRepository.findJobPostOnMap(worker.getId(), northEastLat, northEastLng, southWestLat, southWestLng, techList, dateList, scrap).stream()
                .map(JobPostMapResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 모집 공고 지도에서 조회 (비회원)
     */
    @Transactional(readOnly = true)
    public List<JobPostMapResponse> findJobPostsOnMapForNonMember(Float northEastLat, Float northEastLng, Float southWestLat, Float southWestLng, List<Tech> techList, List<LocalDate> dateList, Boolean scrap) {

        return jobPostRepository.findJobPostOnMap(null, northEastLat, northEastLng, southWestLat, southWestLng, techList, dateList, scrap).stream()
                .map(JobPostMapResponse::from)
                .collect(Collectors.toList());
    }
}
