package jikgong.domain.offer.dtos.worker;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.offerWorkDate.entity.OfferWorkDateStatus;
import jikgong.domain.workDate.dtos.WorkDateResponse;
import jikgong.domain.workDate.entity.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ReceivedOfferResponse {
    private Long offerWorkDateId;
    private OfferWorkDateStatus status; // 제안 status

    private WorkDateResponse workDateResponse; // 날짜 정보
    private JobPostResponse jobPostResponse; // 모집 공고 정보

    public static ReceivedOfferResponse from(OfferWorkDate offerWorkDate) {
        JobPost jobPost = offerWorkDate.getOffer().getJobPost();
        WorkDate workDate = offerWorkDate.getWorkDate();

        return ReceivedOfferResponse.builder()
                .offerWorkDateId(offerWorkDate.getId())
                .status(offerWorkDate.getOfferWorkDateStatus())
                .workDateResponse(WorkDateResponse.from(workDate))
                .jobPostResponse(JobPostResponse.from(jobPost))
                .build();
    }

    @Getter
    @Builder
    public static class JobPostResponse {
        private Tech tech; // 직종
        private String title; // 공고 제목

        public static JobPostResponse from(JobPost jobPost) {
            return JobPostResponse.builder()
                    .tech(jobPost.getTech())
                    .title(jobPost.getTitle())
                    .build();
        }
    }
}
