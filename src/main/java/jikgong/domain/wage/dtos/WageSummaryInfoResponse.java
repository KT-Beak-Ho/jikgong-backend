package jikgong.domain.wage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class WageSummaryInfoResponse {
    private Integer totalWorkTime;
    private Integer totalWage;
}
