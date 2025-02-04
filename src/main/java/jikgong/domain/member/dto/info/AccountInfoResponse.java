package jikgong.domain.member.dto.info;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountInfoResponse {

    private String bank; // 은행
    private String accountHolder; // 예금주
    private String account; // 게좌 번호
}
