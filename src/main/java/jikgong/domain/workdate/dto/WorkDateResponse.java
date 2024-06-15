package jikgong.domain.workdate.dto;

import java.time.LocalDate;
import jikgong.domain.workdate.entity.WorkDate;
import lombok.Builder;
import lombok.Getter;

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
