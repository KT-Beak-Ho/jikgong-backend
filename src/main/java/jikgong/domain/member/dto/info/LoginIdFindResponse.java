package jikgong.domain.member.dto.info;

import jikgong.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginIdFindResponse {

    private String loginId;

    public static LoginIdFindResponse from(Member member) {
        return LoginIdFindResponse.builder()
            .loginId(member.getLoginId())
            .build();
    }
}
