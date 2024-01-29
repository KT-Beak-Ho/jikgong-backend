package jikgong.domain.jobPost.dtos.company;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Builder
public class JobPostResponseForAcceptedApply {
    /**
     * 신청 내역 에서 보여질 JobPost 정보
     */
    private Long postId;
    private String title; // 공고 제목
    private Tech tech; // 기술
    private Integer recruitNum; // 모집 인원
    private LocalTime startTime; // 시작 일시
    private Integer wage; // 임금
    private String address; // 도로명 주소

    public static JobPostResponseForAcceptedApply from(JobPost jobPost) {
        return JobPostResponseForAcceptedApply.builder()
                .postId(jobPost.getId())
                .title(jobPost.getTitle())
                .tech(jobPost.getTech())
                .recruitNum(jobPost.getRecruitNum())
                .startTime(jobPost.getStartTime())
                .wage(jobPost.getWage())
                .address(jobPost.getAddress().getAddress())
                .build();
    }
}
