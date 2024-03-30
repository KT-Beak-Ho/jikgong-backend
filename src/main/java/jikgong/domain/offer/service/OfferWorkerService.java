package jikgong.domain.offer.service;

import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.offer.dtos.worker.OfferProcessRequest;
import jikgong.domain.offer.dtos.worker.ReceivedOfferListResponse;
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

    /**
     * 받은 제안 목록 조회
     * 필터: 대기 중, 마감
     */
    public List<ReceivedOfferListResponse> findReceivedOffer(Long workerId, Boolean isPending) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        List<OfferWorkDate> offerWorkDateList;
        if (isPending) {
            offerWorkDateList = offerWorkDateRepository.findReceivedPendingOffer(worker.getId());
        } else {
            offerWorkDateList = offerWorkDateRepository.findReceivedClosedOffer(worker.getId());
        }
        return offerWorkDateList.stream()
                .map(ReceivedOfferListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 제안 수락, 거절
     * 수락일 경우 출역일 전인지 체크 & 중복 출역 여부 체크
     */
    public void processOffer(Long workerId, OfferProcessRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        OfferWorkDate offerWorkDate = offerWorkDateRepository.findByIdWithWorkDate(request.getOfferWorkDateId())
                .orElseThrow(() -> new CustomException(ErrorCode.OFFER_WORK_DATE_NOT_FOUND));

        // 수락일 때
        if (request.getIsAccept()) {
            WorkDate workDate = offerWorkDate.getWorkDate();

            if (workDate.getRecruitNum() <= workDate.getRegisteredNum()) {
                throw new CustomException(ErrorCode.RECRUITMENT_FULL);
            }

            // 출역일 전 인지 체크
            if (LocalDate.now().isAfter(workDate.getDate())) {
                throw new CustomException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
            }
            if (LocalDate.now().isEqual(workDate.getDate()) && LocalTime.now().isAfter(workDate.getJobPost().getStartTime())) {
                throw new CustomException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
            }

            // 수락 하려는 날짜에 출역 날짜가 확정된 기록이 있는지 체크
            if (applyRepository.findAcceptedApplyByWorkDate(worker.getId(), workDate.getDate()) != 0) {
                throw new CustomException(ErrorCode.APPLY_ALREADY_ACCEPTED_IN_WORKDATE);
            }
        }
        // 수락, 거부 처리
        offerWorkDate.processOffer(request.getIsAccept());

        // 같은 날 다른 공고에 지원 했던 대기중인 요청 취소
        applyRepository.deleteOtherApplyOnDate(worker.getId(), offerWorkDate.getWorkDate().getDate());
    }
}
