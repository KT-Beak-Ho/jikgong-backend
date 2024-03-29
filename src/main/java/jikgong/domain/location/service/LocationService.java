package jikgong.domain.location.service;


import jikgong.domain.common.Address;
import jikgong.domain.location.dtos.LocationDeleteRequest;
import jikgong.domain.location.dtos.LocationResponse;
import jikgong.domain.location.dtos.LocationSaveRequest;
import jikgong.domain.location.dtos.LocationUpdateRequest;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LocationService {
    private final LocationRepository locationRepository;
    private final MemberRepository memberRepository;

    /**
     * 위치 정보 등록
     */
    public Long saveLocation(Long workerId, LocationSaveRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 기존 main location 이 있다면 해지
        if (request.getIsMain()) {
            Optional<Location> mainLocation = locationRepository.findMainLocationByMemberId(worker.getId());
            if (mainLocation.isPresent()) {
                mainLocation.get().changeMainLocation(false);
            }
        }

        Location location = Location.builder()
                .address(new Address(request.getAddress(), request.getLatitude(), request.getLongitude()))
                .isMain(request.getIsMain())
                .member(worker)
                .build();

        return locationRepository.save(location).getId();
    }

    /**
     * 등록된 위치 조회
     */
    @Transactional(readOnly = true)
    public List<LocationResponse> findLocationByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<LocationResponse> locationResponseList = locationRepository.findByMemberId(member.getId()).stream()
                .map(LocationResponse::from)
                .collect(Collectors.toList());

        return locationResponseList;
    }

    /**
     * 대표 위치 변경
     */
    public void changeMainLocation(Long memberId, Long locationId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Location location = locationRepository.findByLocationIdAndMemberId(member.getId(), locationId)
                .orElseThrow(() -> new CustomException(ErrorCode.LOCATION_NOT_FOUND));

        // 요청한 locationId 가 이미 main 이라면
        if (location.getIsMain()) throw new CustomException(ErrorCode.LOCATION_ALREADY_MAIN);

        Optional<Location> mainLocation = locationRepository.findMainLocationByMemberId(memberId);
        if (mainLocation.isPresent()) {
            mainLocation.get().changeMainLocation(false); // 기존 main location
        }
        location.changeMainLocation(true);  // 신규 main location
    }

    /**
     * 위치 수정
     */
    public void updateLocation(Long memberId, LocationUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Location location = locationRepository.findByLocationIdAndMemberId(member.getId(), request.getLocationId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOCATION_NOT_FOUND));

        location.updateLocation(request);
    }

    /**
     * 위치 단일 삭제
     */
    public void deleteLocation(Long memberId, Long locationId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        locationRepository.deleteById(member.getId(), locationId);
    }

    /**
     * 위치 복수 삭제
     */
    public void deleteLocations(Long memberId, LocationDeleteRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        locationRepository.deleteByIdList(member.getId(), request.getLocationIdList());
    }
}