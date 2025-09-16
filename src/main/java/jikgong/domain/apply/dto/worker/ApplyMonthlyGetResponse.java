package jikgong.domain.apply.dto.worker;

import java.time.LocalDate;
import jikgong.domain.apply.entity.ApplyStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyMonthlyGetResponse {

    private LocalDate date; // 신청 내역이 있는 날짜
    private ApplyStatus status; // 확정 or 대기, 거절

    public static ApplyMonthlyGetResponse from(LocalDate date, ApplyStatus status) {
        return ApplyMonthlyGetResponse.builder()
            .date(date)
            .status(status)
            .build();
    }
}
