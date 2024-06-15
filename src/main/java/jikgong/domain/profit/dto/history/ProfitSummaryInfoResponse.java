package jikgong.domain.profit.dto.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ProfitSummaryInfoResponse {
    private Integer totalWorkTime;
    private Integer totalWage;
}
