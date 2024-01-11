package jikgong.domain.jobPost.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class JobPostManageResponse {
    private Long jobPostId;
    private List<LocalDate> workDateList;
    private LocalDate startDate;
    private LocalDate endDate;
}
