package jikgong.domain.jobPost.dtos;

import jikgong.domain.history.dtos.CountHistory;
import jikgong.domain.member.dtos.MemberAcceptedResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class JobPostManageWorkerResponse {
    /**
     * 인력 관리: 확정 인부 관련 dto
     */
    private CountHistory countHistory;
    private PageImpl<MemberAcceptedResponse> memberAcceptedResponsePage;
}
