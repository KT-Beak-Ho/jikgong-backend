package jikgong.domain.apply.dto.worker;

import java.time.LocalDate;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyStatusGetResponse {

    private LocalDate date; // 신청 내역이 있는 날짜
    private ApplyStatus status; // 확정 or 대기, 거절

    public static ApplyStatusGetResponse from(LocalDate date, ApplyStatus status) {
        return ApplyStatusGetResponse.builder()
            .date(date)
            .status(status)
            .build();
    }
}
