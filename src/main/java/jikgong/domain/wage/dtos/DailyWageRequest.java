package jikgong.domain.wage.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class DailyWageRequest {
    private LocalDateTime selectDay; // 선택한 일짜
}
