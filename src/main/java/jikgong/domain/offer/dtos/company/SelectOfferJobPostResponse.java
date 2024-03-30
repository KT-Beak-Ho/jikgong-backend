package jikgong.domain.offer.dtos.company;

import jikgong.domain.jobPost.dtos.offer.JobPostListResponse;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.member.entity.Member;
import jikgong.domain.workDate.dtos.WorkDateResponse;
import jikgong.domain.workDate.entity.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class SelectOfferJobPostResponse {
    private String workerName;
    private List<JobPostListResponse> jobPostListResponseList;

    public static SelectOfferJobPostResponse from(List<JobPost> jobPostList, Member member) {
        List<JobPostListResponse> jobPostListResponseList = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // 여러 공고 한번에 제안 가능
        // 공고 하나당 제안 가능한 날짜 추출
        for (JobPost jobPost : jobPostList) {
            List<WorkDateResponse> availableDate = new ArrayList<>(); // 제안 가능 날짜 리스트
            List<WorkDateResponse> fullCapacityDate = new ArrayList<>(); // 제안 불가능 날짜 (인원 마감)
            List<WorkDateResponse> pastDate = new ArrayList<>(); // 제안 불가능 날짜 (날짜 지남)

            // workDate 조회
            List<WorkDate> jobPostWorkDateList = jobPost.getWorkDateList();
            for (WorkDate workDate : jobPostWorkDateList) {
                // 출역일 전 인지 체크
                if (now.isAfter(workDate.getDate())) {
                    pastDate.add(WorkDateResponse.from(workDate));
                }
                if (now.isEqual(workDate.getDate()) && LocalTime.now().isAfter(jobPost.getStartTime())) {
                    pastDate.add(WorkDateResponse.from(workDate));
                }

                // 모집된 인원이 전부 찼는지 체크
                if (workDate.getRegisteredNum() >= workDate.getRegisteredNum()) {
                    fullCapacityDate.add(WorkDateResponse.from(workDate));
                }

                availableDate.add(WorkDateResponse.from(workDate));
            }
            jobPostListResponseList.add(JobPostListResponse.from(jobPost, availableDate, fullCapacityDate, pastDate));
        }

        return SelectOfferJobPostResponse.builder()
                .workerName(member.getWorkerInfo().getWorkerName())
                .jobPostListResponseList(jobPostListResponseList)
                .build();
    }
}
