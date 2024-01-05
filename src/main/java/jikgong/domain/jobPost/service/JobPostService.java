package jikgong.domain.jobPost.service;

import jikgong.domain.common.Address;
import jikgong.domain.jobPost.dtos.JobPostApplyHistoryResponse;
import jikgong.domain.jobPost.dtos.JobPostListResponse;
import jikgong.domain.jobPost.dtos.JobPostSaveRequest;
import jikgong.domain.jobPost.entity.AvailableInfo;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.JobPostStatus;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.pickupLocation.entity.PickupLocation;
import jikgong.domain.pickupLocation.repository.PickupLocationRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final MemberRepository memberRepository;
    private final PickupLocationRepository pickupLocationRepository;

    public Long saveJobPost(Long memberId, JobPostSaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 모집 공고 저장
        JobPost jobPost = JobPost.builder()
                .tech(request.getTech())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .recruitNum(request.getRecruitNum())
                .wage(request.getWage())
                .workDetail(request.getWorkDetail())
                .preparation(request.getPreparation())
                .expirationTime(request.getExpirationTime())
                .isTemporary(request.getIsTemporary())
                .availableInfo(new AvailableInfo(request.getMeal(), request.getPickup(), request.getSafeEquipment(), request.getPark()))
                .address(new Address(request.getAddress(), request.getLatitude(), request.getLongitude()))
                .member(member)
                .build();

        JobPost savedJobPost = jobPostRepository.save(jobPost);

        // 픽업 정보 저장
        List<PickupLocation> pickupLocationList = request.getPickupList().stream()
                .map(address -> new PickupLocation(address, savedJobPost))
                .collect(Collectors.toList());
        pickupLocationRepository.saveAll(pickupLocationList);

        return savedJobPost.getId();
    }

    // 등록한 공고 리스트
    public List<JobPostListResponse> findJobPostsByMemberId(Long memberId, JobPostStatus jobPostStatus) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        LocalDateTime now = LocalDateTime.now();
        List<JobPost> jobPostList = new ArrayList<>();
        if (jobPostStatus == JobPostStatus.COMPLETED) {
            jobPostList = jobPostRepository.findCompletedJobPostByMemberId(memberId, now);
        }
        else if (jobPostStatus == JobPostStatus.IN_PROGRESS) {
            jobPostList = jobPostRepository.findInProgressJobPostByMemberId(memberId, now);
        }
        else if (jobPostStatus == JobPostStatus.PLANNED) {
            jobPostList = jobPostRepository.findPlannedJobPostByMemberId(memberId, now);
        }

        List<JobPostListResponse> jobPostListResponseList = jobPostList.stream()
                .map(JobPostListResponse::from)
                .collect(Collectors.toList());

        return jobPostListResponseList;
    }
}
