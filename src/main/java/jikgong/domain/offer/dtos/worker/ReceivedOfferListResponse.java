package jikgong.domain.offer.dtos.worker;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.offer.entity.Offer;
import jikgong.domain.offer.entity.OfferStatus;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.offerWorkDate.entity.OfferWorkStatus;
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
    private Tech tech; // 직종
    private LocalDate workDate; // 근무 일짜
    private String title; // 공고 제목
    private OfferWorkStatus status; // 제안 status

    public static ReceivedOfferListResponse from(OfferWorkDate offerWorkDate) {
        JobPost jobPost = offerWorkDate.getOffer().getJobPost();
        WorkDate workDate = offerWorkDate.getWorkDate();

        return ReceivedOfferListResponse.builder()
                .offerWorkDateId(offerWorkDate.getId())
                .tech(jobPost.getTech())
                .workDate(workDate.getWorkDate())
                .title(jobPost.getTitle())
                .status(offerWorkDate.getOfferWorkStatus())
                .build();
    }
}
