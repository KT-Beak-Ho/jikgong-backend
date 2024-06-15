package jikgong.domain.profit.dto.graph;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class DailyGraphResponse {

    private List<Object[]> totalWagePerDay;
    private Map<LocalDate, WorkTimeGraphResponse> totalWorkTimePerDay;
}
