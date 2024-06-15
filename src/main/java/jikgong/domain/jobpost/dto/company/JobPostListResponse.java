package jikgong.domain.jobpost.dto.company;

import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.workdate.entity.WorkDate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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

    public static JobPostListResponse from(JobPost jobPost) {
        int applyNum = 0;
        int acceptedNum = 0;
        for (WorkDate workDate : jobPost.getWorkDateList()) {
            applyNum += workDate.getApplyList().size();
            acceptedNum += workDate.getRegisteredNum();
        }
        return JobPostListResponse.builder()
            .jobPostId(jobPost.getId())
            .title(jobPost.getTitle())
            .createdDate(jobPost.getCreatedDate())
            .applyNum(applyNum)
            .acceptedNum(acceptedNum)
            .build();
    }
}
