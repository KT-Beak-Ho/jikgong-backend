package jikgong.domain.member.service;

import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.member.dto.company.CompanySearchResponse;
import jikgong.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberCompanyService {

    private final MemberRepository memberRepository;

    /**
     * 기업 검색
     */
    @Transactional(readOnly = true)
    public List<CompanySearchResponse> searchCompany(String keyword) {
        return memberRepository.findByCompanyName(keyword).stream()
            .map(CompanySearchResponse::from)
            .collect(Collectors.toList());
    }
}
