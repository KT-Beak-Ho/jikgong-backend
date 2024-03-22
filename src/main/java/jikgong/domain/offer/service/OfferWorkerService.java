package jikgong.domain.offer.service;

import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.offer.dtos.worker.OfferProcessRequest;
import jikgong.domain.offer.dtos.worker.ReceivedOfferListResponse;
import jikgong.domain.offer.repository.OfferRepository;
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
import java.util.ArrayList;
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

            // 출역일 1일 전까지 수락 가능
            if (!workDate.getWorkDate().isAfter(LocalDate.now())) {
                throw new CustomException(ErrorCode.OFFER_PROCESS_NEED_TO_ONE_DAY_AGO);
            }

            int cntAcceptedApply = applyRepository.findAcceptedApplyByWorkDate(worker.getId(), workDate.getWorkDate());
            if (cntAcceptedApply != 0) {
                throw new CustomException(ErrorCode.APPLY_ALREADY_ACCEPTED_IN_WORKDATE);
            }
        }
        // 수락, 거부 처리
        offerWorkDate.processOffer(request.getIsAccept());
    }
}
