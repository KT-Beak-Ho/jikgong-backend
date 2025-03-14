package jikgong.domain.offer.dto.worker;

import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.offer.entity.OfferWorkDate;
import jikgong.domain.offer.entity.OfferWorkDateStatus;
import jikgong.domain.workdate.dto.WorkDateResponse;
import jikgong.domain.workdate.entity.WorkDate;
import jikgong.domain.workexperience.entity.Tech;
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
