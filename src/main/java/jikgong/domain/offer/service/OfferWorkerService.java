package jikgong.domain.offer.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.offer.dtos.worker.OfferJobPostResponse;
import jikgong.domain.offer.dtos.worker.OfferProcessRequest;
import jikgong.domain.offer.dtos.worker.ReceivedOfferResponse;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.offerWorkDate.repository.OfferWorkDateRepository;
import jikgong.domain.workDate.entity.WorkDate;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OfferWorkerService {
    private final MemberRepository memberRepository;
    private final OfferWorkDateRepository offerWorkDateRepository;
    private final ApplyRepository applyRepository;
    private final LocationRepository locationRepository;

    /**
     * 받은 제안 목록 조회
     * 필터: 대기 중, 마감
     */
    @Transactional(readOnly = true)
    public List<ReceivedOfferResponse> findReceivedOffer(Long workerId, Boolean isPending) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        List<OfferWorkDate> offerWorkDateList;
        if (isPending) {
            // 대기 중인 제안 목록
            offerWorkDateList = offerWorkDateRepository.findReceivedPendingOffer(worker.getId());
        } else {
            // 처리된 제안 목록
            offerWorkDateList = offerWorkDateRepository.findReceivedClosedOffer(worker.getId());
        }
        return offerWorkDateList.stream()
                .map(ReceivedOfferResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 제안 수락, 거절
     * 수락일 경우 출역일 전인지 체크 & 중복 출역 여부 체크
     * 제안을 수락하는 경우는 출역 시간 직전까지 수락 가능
     * 모집된 인원 증가
     */
    public void processOffer(Long workerId, OfferProcessRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        OfferWorkDate offerWorkDate = offerWorkDateRepository.findByIdWithWorkDate(request.getOfferWorkDateId())
                .orElseThrow(() -> new CustomException(ErrorCode.OFFER_WORK_DATE_NOT_FOUND));

        // 수락일 때
        if (request.getIsAccept()) {
            WorkDate workDate = offerWorkDate.getWorkDate();
            // 인원 마감 체크
            if (workDate.getRecruitNum() <= workDate.getRegisteredNum()) {
                throw new CustomException(ErrorCode.RECRUITMENT_FULL);
            }

            // 출역 시각 3시간 전까지 처리 가능
            if (LocalDate.now().isAfter(workDate.getDate())) {
                throw new CustomException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
            }
            if (LocalDate.now().isEqual(workDate.getDate()) && LocalTime.now().plusHours(3L).isAfter(workDate.getJobPost().getStartTime())) {
                throw new CustomException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
            }

            // 수락 하려는 날짜에 출역 날짜가 확정된 기록이 있는지 체크
            if (applyRepository.findAcceptedApplyByWorkDate(worker.getId(), workDate.getDate()) != 0) {
                throw new CustomException(ErrorCode.APPLY_ALREADY_ACCEPTED_IN_WORKDATE);
            }

            // 모집된 인원 증가
            workDate.plusRegisteredNum(1);
        }
        // 수락, 거부 처리
        offerWorkDate.processOffer(request.getIsAccept());

        // 같은 날 다른 공고에 지원 했던 대기중인 요청 취소
        List<Long> cancelApplyIdList = applyRepository.deleteOtherApplyOnDate(worker.getId(), offerWorkDate.getWorkDate().getDate());
        int canceledCount = applyRepository.updateApplyStatus(cancelApplyIdList, ApplyStatus.CANCELED, LocalDateTime.now());
        log.info("취소된 요청 횟수: " + canceledCount);
    }

    /**
     * 모집 공고 상세 화면
     * 일자리 제안 시 보여 줄 상세 화면
     */
    @Transactional(readOnly = true)
    public OfferJobPostResponse getJobPostDetailForOffer(Long workerId, Long offerWorkDateId) {
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

        return OfferJobPostResponse.from(offerWorkDate, acceptedApply, location);
    }
}
