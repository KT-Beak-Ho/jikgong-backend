package jikgong.domain.history.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CountHistory {
    private Long allCount;
    private Long workCount;
    private Long notWorkCount;
}
