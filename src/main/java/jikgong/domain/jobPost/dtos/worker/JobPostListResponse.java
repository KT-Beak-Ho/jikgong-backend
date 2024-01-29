package jikgong.domain.jobPost.dtos.worker;

import jikgong.domain.jobPost.entity.Tech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class JobPostListResponse {
    private Long jobPostId;

    private Tech tech; // 직종
    private Integer recruitNum; // 모집 인원
    private String title; // 공고 제목

    private LocalDate startDate; // 시작 날짜
    private LocalDate endDate; // 종료 날짜
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private String address; // 도로명 주소
    private Double distance; // 거리

    private String companyName;
    private Integer wage; // 임금
}
