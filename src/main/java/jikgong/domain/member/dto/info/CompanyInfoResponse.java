package jikgong.domain.member.dto.info;

import jikgong.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CompanyInfoResponse {

    private String loginId; // 로그인 아이디
    private String phone; // 휴대폰 번호

    private String bank; // 은행 종류
    private String account; // 계좌 번호
    private String businessNumber; // 사업자 번호
    private String region; // 지역
    private String companyName; // 회사 명
    private String email; // 이메일
    private String manager; // 담당자 이름

    public static CompanyInfoResponse from(Member company) {
        return CompanyInfoResponse.builder()
            .loginId(company.getLoginId())
            .phone(company.getPhone())

            .bank(company.getCompanyInfo().getBank())
            .bank(company.getCompanyInfo().getAccount())
            .businessNumber(company.getCompanyInfo().getBusinessNumber())
            .region(company.getCompanyInfo().getRegion())
            .companyName(company.getCompanyInfo().getCompanyName())
            .email(company.getEmail())
            .manager(company.getCompanyInfo().getManager())

            .build();
    }

}
