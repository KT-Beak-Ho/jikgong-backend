package jikgong.domain.apply.dtos.worker;

import jikgong.domain.apply.entity.ApplyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
public class ApplyResponseMonthly {
    private LocalDate date; // 신청 내역이 있는 날짜
    private ApplyStatus status; // 확정 or 대기, 거절

    public static ApplyResponseMonthly from(LocalDate date, ApplyStatus status) {
        return ApplyResponseMonthly.builder()
                .date(date)
                .status(status)
                .build();
    }
}
