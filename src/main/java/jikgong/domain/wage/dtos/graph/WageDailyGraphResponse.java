package jikgong.domain.wage.dtos.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@AllArgsConstructor
@Getter
@Builder
public class WageDailyGraphResponse {
    private Map<LocalDate, Integer> wageMap;
    private Map<LocalDate, WorkTimeGraphResponse> workTimeMap;
}
