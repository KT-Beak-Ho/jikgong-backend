package jikgong.domain.member.dto.info;

import jikgong.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountInfoResponse {

    private String bank; // 은행
    private String accountHolder; // 예금주
    private String account; // 게좌 번호

    public static AccountInfoResponse from(Member member) {
        return AccountInfoResponse.builder()
            .bank(member.getWorkerInfo().getBank())
            .accountHolder(member.getWorkerInfo().getAccountHolder())
            .account(member.getWorkerInfo().getAccount())
            .build();
    }
}
