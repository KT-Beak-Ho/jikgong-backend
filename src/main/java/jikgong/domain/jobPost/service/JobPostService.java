package jikgong.domain.jobPost.service;

import jikgong.domain.common.Address;
import jikgong.domain.jobPost.dtos.JobPostSaveRequest;
import jikgong.domain.jobPost.entity.AvailableInfo;
import jikgong.domain.jobPost.entity.JobPost;
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
                .recruitNum(request.getRecruitNum())
                .wage(request.getWage())
                .workDetail(request.getWorkDetail())
                .preparation(request.getPreparation())
                .expirationTime(request.getExpirationTime())
                .address(new Address(request.getAddress(), request.getLatitude(), request.getLongitude()))
                .availableInfo(new AvailableInfo(request.getMeal(), request.getPickup(), request.getSafeEquipment(), request.getPark()))
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
}
