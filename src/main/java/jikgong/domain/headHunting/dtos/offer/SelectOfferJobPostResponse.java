package jikgong.domain.headHunting.dtos.offer;

import jikgong.domain.jobPost.dtos.headhunting.JobPostListResponse;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.member.entity.Member;
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

        for (JobPost jobPost : jobPostList) {
            List<LocalDate> availableDate = new ArrayList<>();
            List<WorkDate> jobPostWorkDateList = jobPost.getWorkDateList();
            for (WorkDate jobPostWorkDate : jobPostWorkDateList) {
                // 노동자가 이미 일하는 날짜를 제외
                if (!cantWorkDateSet.contains(jobPostWorkDate.getWorkDate())) {
                    availableDate.add(jobPostWorkDate.getWorkDate());
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
