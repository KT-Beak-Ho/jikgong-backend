package jikgong.domain.jobPost.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.jobPost.dtos.offer.JobPostDetailResponseForOffer;
import jikgong.domain.jobPost.dtos.worker.JobPostDetailResponse;
import jikgong.domain.jobPost.dtos.worker.JobPostListResponse;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.SortType;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.offerWorkDate.repository.OfferWorkDateRepository;
import jikgong.domain.workDate.entity.WorkDate;
import jikgong.domain.workDate.repository.WorkDateRepository;
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
    public Page<JobPostListResponse> getMainPage(Long workerId, Tech tech, List<LocalDate> workDateList, Boolean scrap, Boolean meal, Park park, SortType sortType, Pageable pageable) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Location location = locationRepository.findMainLocationByMemberId(worker.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOCATION_NOT_FOUND));

        // querydsl
        Page<JobPostListResponse> jobPostPage = jobPostRepository.getMainPage(worker.getId(), tech, workDateList, scrap, meal, park, location, sortType, pageable);

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
     * 모집 공고 상세 화면
     * 일자리 제안 시 보여 줄 상세 화면
     */
    @Transactional(readOnly = true)
    public JobPostDetailResponseForOffer getJobPostDetailForOffer(Long workerId, Long offerWorkDateId) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        OfferWorkDate offerWorkDate = offerWorkDateRepository.findByIdAtProcessOffer(offerWorkDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.OFFER_WORK_DATE_NOT_FOUND));

        WorkDate workDate = offerWorkDate.getWorkDate();

        // 제안 날 확정된 지원 내역 조회
        List<Apply> acceptedApply = applyRepository.checkAcceptedApplyForOffer(worker.getId(), workDate.getDate());

        // 대표 위치 조회
        Location location = locationRepository.findMainLocationByMemberId(worker.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOCATION_NOT_FOUND));

        return JobPostDetailResponseForOffer.from(offerWorkDate, acceptedApply, location);
    }
}
