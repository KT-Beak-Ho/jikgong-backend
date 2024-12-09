package jikgong.domain.jobpost.dto.company;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.workdate.dto.WorkDateResponse;
import jikgong.domain.workdate.entity.WorkDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobPostListResponse {

    /**
     * 등록한 일자리 공고 목록 dto
     */
    private Long jobPostId;
    private String title;
    private LocalDateTime createdDate;
    private Integer applyNum;
    private Integer acceptedNum;

    private List<WorkDateResponse> workDateResponseList;

    public static JobPostListResponse from(JobPost jobPost) {
        int applyNum = 0;
        int acceptedNum = 0;
        for (WorkDate workDate : jobPost.getWorkDateList()) {
            applyNum += workDate.getApplyList().size();
            acceptedNum += workDate.getRegisteredNum();
        }

        List<WorkDateResponse> workDateResponseList = jobPost.getWorkDateList().stream()
            .map(WorkDateResponse::from)
            .collect(Collectors.toList());

        return JobPostListResponse.builder()
            .jobPostId(jobPost.getId())
            .title(jobPost.getTitle())
            .createdDate(jobPost.getCreatedDate())
            .applyNum(applyNum)
            .acceptedNum(acceptedNum)

            .workDateResponseList(workDateResponseList)
            .build();
    }
}
