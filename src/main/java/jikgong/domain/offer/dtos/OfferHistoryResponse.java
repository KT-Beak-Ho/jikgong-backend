package jikgong.domain.offer.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.offer.entity.Offer;
import jikgong.domain.offer.entity.OfferStatus;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.workDate.entity.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class OfferHistoryResponse {
    private Long offerId;
    private String title; // 공고 명
    private String workerName; // 노동자 이름
    private Tech tech; // 기술 타입
    private List<LocalDate> workDateList;

    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private Integer wage; // 임금
    private OfferStatus offerStatus; // 수락, 거절

    public static OfferHistoryResponse from(Offer offer) {
        List<WorkDate> workDateList = offer.getOfferWorkDateList().stream()
                .map(OfferWorkDate::getWorkDate)
                .collect(Collectors.toList());


        JobPost jobPost = workDateList.get(0).getJobPost();
        Member worker = offer.getWorker();

        return OfferHistoryResponse.builder()
                .offerId(offer.getId())
                .title(jobPost.getTitle())
                .workerName(worker.getWorkerInfo().getWorkerName())
                .tech(jobPost.getTech())
                .workDateList(workDateList.stream().map(WorkDate::getWorkDate).collect(Collectors.toList()))
                .startTime(jobPost.getStartTime())
                .endTime(jobPost.getEndTime())
                .wage(jobPost.getWage())
                .offerStatus(offer.getOfferStatus())
                .build();
    }
}
