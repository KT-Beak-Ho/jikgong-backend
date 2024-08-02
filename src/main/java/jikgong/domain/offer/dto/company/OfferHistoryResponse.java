package jikgong.domain.offer.dto.company;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.offer.entity.Offer;
import jikgong.domain.offer.entity.OfferStatus;
import jikgong.domain.offerworkdate.entity.OfferWorkDate;
import jikgong.domain.workdate.dto.WorkDateResponse;
import jikgong.domain.workdate.entity.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class OfferHistoryResponse {

    private Long offerId;
    private OfferStatus offerStatus; // 수락, 거절

    private List<WorkDateResponse> workDateResponseList; // 날짜 정보
    private JobPostResponse jobPostResponse; // 모집 공고 정보
    private MemberResponse memberResponse; // 노동자 정보

    public static OfferHistoryResponse from(Offer offer) {
        List<WorkDate> workDateList = offer.getOfferWorkDateList().stream()
            .map(OfferWorkDate::getWorkDate)
            .sorted(Comparator.comparing(WorkDate::getDate))
            .collect(Collectors.toList());

        JobPost jobPost = offer.getJobPost();
        Member worker = offer.getWorker();

        return OfferHistoryResponse.builder()
            .offerId(offer.getId())
            .offerStatus(offer.getOfferStatus())
            .workDateResponseList(workDateList.stream().map(WorkDateResponse::from).collect(Collectors.toList()))
            .jobPostResponse(JobPostResponse.from(jobPost))
            .memberResponse(MemberResponse.from(worker))
            .build();
    }


    @Getter
    @Builder
    public static class JobPostResponse {

        private String title; // 공고 명
        private Tech tech; // 기술 타입
        private LocalTime startTime; // 시작 시간
        private LocalTime endTime; // 종료 시간
        private Integer wage; // 임금

        public static JobPostResponse from(JobPost jobPost) {
            return JobPostResponse.builder()
                .title(jobPost.getTitle())
                .tech(jobPost.getTech())
                .startTime(jobPost.getStartTime())
                .endTime(jobPost.getEndTime())
                .wage(jobPost.getWage())
                .build();
        }
    }

    @Getter
    @Builder
    public static class MemberResponse {

        private String workerName; // 노동자 이름

        public static MemberResponse from(Member member) {
            return MemberResponse.builder().workerName(member.getWorkerInfo().getWorkerName()).build();
        }
    }
}
