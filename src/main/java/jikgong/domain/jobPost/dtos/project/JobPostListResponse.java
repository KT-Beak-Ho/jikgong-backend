package jikgong.domain.jobPost.dtos.project;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.workDate.entity.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Builder
public class JobPostListResponse {
    private Tech tech; // 직종
    private Integer wage; // 임금

    private LocalDate date; // 출역일
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간

    private Integer recruitNum; // 모집 인원
    private Integer registeredNum; // 모집된 인원

    // 가능 여부
    private Boolean meal; // 식사 제공 여부
    private Boolean pickup; // 픽업 여부
    private Park park; // 주차 가능 여부

    // 기업 정보
    private String companyName;

    public static JobPostListResponse from(WorkDate workDate) {
        JobPost jobPost = workDate.getJobPost();
        Member company = jobPost.getMember();

        return JobPostListResponse.builder()
                .tech(jobPost.getTech())
                .wage(jobPost.getWage())
                .date(workDate.getDate())
                .startTime(jobPost.getStartTime())
                .endTime(jobPost.getEndTime())
                .recruitNum(workDate.getRecruitNum())
                .registeredNum(workDate.getRegisteredNum())
                .meal(jobPost.getAvailableInfo().getMeal())
                .pickup(jobPost.getAvailableInfo().getPickup())
                .park(jobPost.getAvailableInfo().getPark())
                .companyName(company.getCompanyInfo().getCompanyName())
                .build();
    }
}
