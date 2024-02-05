package jikgong.global.security.service;

import jikgong.global.security.principal.MemberDto;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Cacheable(cacheNames = "loginMember", key = "#phone", condition = "#phone != null", cacheManager = "authCacheManager")
    public PrincipalDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        log.info("loadUserByUsername 실행");
        Member findMember = memberRepository.findByPhone(phone)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        MemberDto memberDto = MemberDto.builder()
                .id(findMember.getId())
                .role(findMember.getRole())
                .build();
        return new PrincipalDetails(memberDto);
    }
}
