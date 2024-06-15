package jikgong.domain.member.dto;

import jikgong.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CompanySearchResponse {
    private Long companyId; // 기업 pk
    private String companyName; // 기업명

    public static CompanySearchResponse from(Member member) {
        return CompanySearchResponse.builder()
                .companyId(member.getId())
                .companyName(member.getCompanyInfo().getCompanyName())
                .build();
    }
}
