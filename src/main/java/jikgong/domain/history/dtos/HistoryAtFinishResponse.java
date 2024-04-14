package jikgong.domain.history.dtos;

import jikgong.domain.member.dtos.history.MemberAcceptedResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class HistoryAtFinishResponse {
    private List<MemberAcceptedResponse> workMemberResponse;
    private List<MemberAcceptedResponse> notWorkMemberResponse;
}
