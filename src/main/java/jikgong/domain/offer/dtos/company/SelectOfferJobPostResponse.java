package jikgong.domain.offer.dtos.company;

import jikgong.domain.jobPost.dtos.headhunting.JobPostListResponse;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.member.entity.Member;
import jikgong.domain.workDate.dtos.WorkDateResponse;
import jikgong.domain.workDate.entity.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@Builder
public class SelectOfferJobPostResponse {
    private String workerName;
    private List<JobPostListResponse> jobPostListResponseList;

    public static SelectOfferJobPostResponse from(List<JobPost> jobPostList, Member member, Set<LocalDate> cantWorkDateSet) {
        List<JobPostListResponse> jobPostListResponseList = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (JobPost jobPost : jobPostList) {
            List<WorkDateResponse> availableDate = new ArrayList<>();
            List<WorkDate> jobPostWorkDateList = jobPost.getWorkDateList();
            for (WorkDate jobPostWorkDate : jobPostWorkDateList) {
                // 노동자가 이미 일하는 날짜를 제외
                // 당일, 과거 날짜 제외
                if (!cantWorkDateSet.contains(jobPostWorkDate.getDate()) && jobPostWorkDate.getDate().isAfter(now)) {
                    availableDate.add(WorkDateResponse.from(jobPostWorkDate));
                }
            }
            jobPostListResponseList.add(JobPostListResponse.from(jobPost, availableDate));
        }

        return SelectOfferJobPostResponse.builder()
                .workerName(member.getWorkerInfo().getWorkerName())
                .jobPostListResponseList(jobPostListResponseList)
                .build();
    }
}
