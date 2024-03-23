package jikgong.domain.workDate.dtos;

import jikgong.domain.workDate.entity.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
public class WorkDateResponse {
    private Long workDateId;
    private LocalDate date;

    public static WorkDateResponse from(WorkDate workDate) {
        return WorkDateResponse.builder()
                .workDateId(workDate.getId())
                .date(workDate.getDate())
                .build();
    }
}
