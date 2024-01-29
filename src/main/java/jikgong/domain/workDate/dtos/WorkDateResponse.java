package jikgong.domain.workDate.dtos;

import jikgong.domain.workDate.entity.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class WorkDateResponse {
    private Long workDateId;
    private LocalDate workDate;

    public static WorkDateResponse from(WorkDate workDate) {
        return WorkDateResponse.builder()
                .workDateId(workDate.getId())
                .workDate(workDate.getWorkDate())
                .build();
    }
}
