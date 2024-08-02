package jikgong.domain.jobpost.dto.company;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.workdate.dto.WorkDateResponse;
import jikgong.domain.workdate.entity.WorkDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobPostResponseForOffer {

    private String workerName;
    private List<JobPostResponse> jobPostResponseList;

    public static JobPostResponseForOffer from(List<JobPost> jobPostList, Member member) {
        List<JobPostResponse> jobPostResponseList = new ArrayList<>();

        // 여러 공고 한번에 제안 가능
        // 공고 하나당 제안 가능한 날짜 추출
        for (JobPost jobPost : jobPostList) {
            jobPostResponseList.add(JobPostResponse.from(jobPost));
        }
        return JobPostResponseForOffer.builder()
            .workerName(member.getWorkerInfo().getWorkerName())
            .jobPostResponseList(jobPostResponseList)
            .build();
    }

    @Getter
    @Builder
    public static class JobPostResponse {

        private Long jobPostId;
        private Tech tech; // 타입
        private String title; // 모집 공고 제목
        private List<WorkDateResponse> availableDate; // 제안 가능 날짜 리스트
        private List<WorkDateResponse> fullCapacityDate; // 제안 불가능 날짜 (인원 마감)
        private List<WorkDateResponse> pastDate; // 제안 불가능 날짜 (날짜 지남)
        private LocalTime startTime; // 시작 시간
        private LocalTime endTime; // 종료 시간
        private Integer wage; // 임금

        public static JobPostResponse from(JobPost jobPost) {
            LocalDate now = LocalDate.now();

            List<WorkDateResponse> availableDate = new ArrayList<>(); // 제안 가능 날짜 리스트
            List<WorkDateResponse> fullCapacityDate = new ArrayList<>(); // 제안 불가능 날짜 (인원 마감)
            List<WorkDateResponse> pastDate = new ArrayList<>(); // 제안 불가능 날짜 (날짜 지남)

            // workDate 조회
            List<WorkDate> jobPostWorkDateList = jobPost.getWorkDateList()
                .stream()
                .sorted(Comparator.comparing(WorkDate::getDate))
                .collect(Collectors.toList());

            for (WorkDate workDate : jobPostWorkDateList) {
                // 출역일 전 혹은 출역일과 같은 날이라면 출역 시각 3시간 전인지 체크
                if (now.isAfter(workDate.getDate()) ||
                    (now.isEqual(workDate.getDate()) && LocalTime.now().plusHours(3L)
                        .isAfter(jobPost.getStartTime()))) {
                    pastDate.add(WorkDateResponse.from(workDate));
                }

                // 모집된 인원이 전부 찼는지 체크
                else if (workDate.getRegisteredNum() >= workDate.getRecruitNum()) {
                    fullCapacityDate.add(WorkDateResponse.from(workDate));
                } else {
                    availableDate.add(WorkDateResponse.from(workDate));
                }
            }

            return JobPostResponse.builder()
                .jobPostId(jobPost.getId())
                .tech(jobPost.getTech())
                .title(jobPost.getTitle())
                .availableDate(availableDate)
                .fullCapacityDate(fullCapacityDate)
                .pastDate(pastDate)
                .startTime(jobPost.getStartTime())
                .endTime(jobPost.getEndTime())
                .wage(jobPost.getWage())
                .build();
        }
    }
}
