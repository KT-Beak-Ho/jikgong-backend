package jikgong.domain.profit.dto.graph;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MonthlyGraphResponse {

    private List<Object[]> totalWageAndWorkTimePerMonth;
}
