package jikgong.domain.history.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CountHistoryResponse {
    private Long allCount;
    private Long workCount;
    private Long notWorkCount;
}
