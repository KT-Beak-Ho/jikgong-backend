package jikgong.domain.profit.dto.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MonthlyGraphResponse {
    private List<Object[]> totalWageAndWorkTimePerMonth;
}
