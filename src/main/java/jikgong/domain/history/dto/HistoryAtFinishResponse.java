package jikgong.domain.history.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HistoryAtFinishResponse {
    private List<HistoryManageResponse> workMemberResponse;
    private List<HistoryManageResponse> notWorkMemberResponse;
}
