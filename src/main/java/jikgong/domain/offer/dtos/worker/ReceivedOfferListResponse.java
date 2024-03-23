package jikgong.domain.offer.dtos.worker;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.offerWorkDate.entity.OfferWorkDateStatus;
import jikgong.domain.workDate.entity.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
public class ReceivedOfferListResponse {
    private Long offerWorkDateId;
    private Long workDateId; //
    private Tech tech; // 직종
    private LocalDate workDate; // 근무 일짜
    private String title; // 공고 제목
    private OfferWorkDateStatus status; // 제안 status

    public static ReceivedOfferListResponse from(OfferWorkDate offerWorkDate) {
        JobPost jobPost = offerWorkDate.getOffer().getJobPost();
        WorkDate workDate = offerWorkDate.getWorkDate();

        return ReceivedOfferListResponse.builder()
                .offerWorkDateId(offerWorkDate.getId())
                .workDateId(workDate.getId())
                .tech(jobPost.getTech())
                .workDate(workDate.getDate())
                .title(jobPost.getTitle())
                .status(offerWorkDate.getOfferWorkDateStatus())
                .build();
    }
}
