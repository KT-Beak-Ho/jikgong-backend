package jikgong.domain.apply.dtos;

import jikgong.domain.member.dtos.MemberAcceptedResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class ApplyAcceptedResponse {
    private Integer totalCount;
    private Integer workCount;
    private Integer notWorkCount;
    private List<MemberAcceptedResponse> memberResponseList;
}
