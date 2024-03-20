package jikgong.domain.offer.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.offer.dtos.worker.ReceivedOfferListResponse;
import jikgong.domain.offer.repository.OfferRepository;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.offerWorkDate.repository.OfferWorkDateRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<ReceivedOfferListResponse> findReceivedOffer(Long memberId, Boolean isPending) {
        Member worker = memberRepository.findById(memberId)
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
}
