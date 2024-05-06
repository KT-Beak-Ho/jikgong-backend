package jikgong.global.security.principal;

import jikgong.domain.member.entity.Role;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class MemberDto {
    private Long id;
    private Role role;
}
