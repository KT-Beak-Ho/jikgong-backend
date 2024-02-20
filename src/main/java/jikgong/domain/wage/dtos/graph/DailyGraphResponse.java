package jikgong.domain.wage.dtos.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Builder
public class DailyGraphResponse {
    private List<Object[]> totalWagePerDay;
    private Map<LocalDate, WorkTimeGraphResponse> totalWorkTimePerDay;
}
