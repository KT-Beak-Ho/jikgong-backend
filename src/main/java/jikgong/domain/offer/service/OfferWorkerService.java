package jikgong.domain.offer.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.offer.dto.worker.OfferJobPostResponse;
import jikgong.domain.offer.dto.worker.OfferProcessRequest;
import jikgong.domain.offer.dto.worker.ReceivedOfferResponse;
import jikgong.domain.offerworkdate.entity.OfferWorkDate;
import jikgong.domain.offerworkdate.repository.OfferWorkDateRepository;
import jikgong.domain.workdate.entity.WorkDate;
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
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
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
     * 수락일 경우 날짜 & 중복 출역 여부 체크, Apply status 값 업데이트: OFFERED(제안됨) => ACCEPTED(승인됨)
     * 거절일 경우 Offered Apply 제거
     * 모집된 인원 증가
     */
    public void processOffer(Long workerId, OfferProcessRequest request) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        OfferWorkDate offerWorkDate = offerWorkDateRepository.findByIdWithWorkDate(request.getOfferWorkDateId())
            .orElseThrow(() -> new JikgongException(ErrorCode.OFFER_WORK_DATE_NOT_FOUND));

        // workDate 조회
        WorkDate workDate = offerWorkDate.getWorkDate();

        // 회원의 apply에 대한 Lock
        // 하루에 확정된 일자리는 하나임을 만족하기 위함
        applyRepository.findByMemberForLock(worker.getId(), workDate.getDate());

        // 요청에 따라 Apply 삭제 or status 업데이트
        Apply offeredApply = applyRepository.findOfferedApply(worker.getId(), workDate.getId())
            .orElseThrow(() -> new JikgongException(ErrorCode.APPLY_OFFERED_NOT_FOUND));

        // 수락일 때
        if (request.getIsAccept()) {
            // 수락 시 조건에 맞는지 확인
            validationBeforeProcessOffer(workDate, worker);

            // 모집된 인원 증가
            workDate.plusRegisteredNum(1);

            // 제안 받으며 자동으로 생긴 지원 기록 승인 처리
            offeredApply.updateStatus(ApplyStatus.ACCEPTED, LocalDateTime.now());

            // 같은 날 다른 공고에 지원 했던 대기중인 요청 취소
            List<Long> cancelApplyIdList = applyRepository.deleteOtherApplyOnDate(worker.getId(),
                offerWorkDate.getWorkDate().getDate());
            int canceledCount = applyRepository.updateApplyStatus(cancelApplyIdList, ApplyStatus.CANCELED,
                LocalDateTime.now());
            log.info("취소된 요청 횟수: " + canceledCount);
        }
        // 거절일 때
        else {
            // 제안 받으며 자동으로 생긴 지원 기록 제거
            applyRepository.delete(offeredApply);
        }

        // 수락 or 거부 처리
        offerWorkDate.processOffer(request.getIsAccept());
    }

    // 수락 시 조건에 맞는지 확인
    private void validationBeforeProcessOffer(WorkDate workDate, Member worker) {
        // 인원 마감 체크
        if (workDate.getRecruitNum() <= workDate.getRegisteredNum()) {
            throw new JikgongException(ErrorCode.WORK_DATE_RECRUITMENT_FULL);
        }

        // 출역일 최소 2일전 승인
        if (LocalDate.now().isAfter(workDate.getDate().minusDays(2L))) {
            throw new JikgongException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
        }

        // 수락 하려는 날짜에 출역 날짜가 확정된 기록이 있는지 체크
        if (applyRepository.findAcceptedApplyByWorkDate(worker.getId(), workDate.getDate()) != 0) {
            throw new JikgongException(ErrorCode.APPLY_ALREADY_ACCEPTED_IN_WORK_DATE);
        }
    }

    /**
     * 모집 공고 상세 화면
     * 일자리 제안 시 보여 줄 상세 화면
     */
    @Transactional(readOnly = true)
    public OfferJobPostResponse getJobPostDetailForOffer(Long workerId, Long offerWorkDateId) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        OfferWorkDate offerWorkDate = offerWorkDateRepository.findByIdAtProcessOffer(offerWorkDateId)
            .orElseThrow(() -> new JikgongException(ErrorCode.OFFER_WORK_DATE_NOT_FOUND));

        WorkDate workDate = offerWorkDate.getWorkDate();

        // 제안 날 확정된 지원 내역 조회
        List<Apply> acceptedApply = applyRepository.checkAcceptedApplyForOffer(worker.getId(), workDate.getDate());

        // 대표 위치 조회
        Location location = locationRepository.findMainLocationByMemberId(worker.getId())
            .orElseThrow(() -> new JikgongException(ErrorCode.LOCATION_NOT_FOUND));

        return OfferJobPostResponse.from(offerWorkDate, acceptedApply, location);
    }
}
