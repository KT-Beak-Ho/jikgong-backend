package jikgong.domain.member.fixture;

import jikgong.domain.member.dto.join.JoinCompanyRequest;
import jikgong.domain.member.entity.Role;

public class JoinCompanyRequestFixture {

    public static JoinCompanyRequest createDefault() {
        return JoinCompanyRequest.builder()
            .loginId("abcdefg1")
            .password("password123")
            .phone("01012345678")
            .role(Role.ROLE_COMPANY)
            .privacyConsent(true)
            .deviceToken("sampleDeviceToken")
            .isNotification(true)
            .businessNumber("00000000")
            .region("서울")
            .companyName("삼성")
            .email("jaeyoung@naver.com")
            .manager("이재용")
            .requestContent("직공 서비스에 가입하고 싶습니다.")
            .build();
    }

    public static JoinCompanyRequest createWithCustomData(
        String loginId,
        String password,
        String phone,
        Role role,
        Boolean privacyConsent,
        String deviceToken,
        Boolean isNotification,
        String businessNumber,
        String region,
        String companyName,
        String email,
        String manager,
        String requestContent
    ) {
        return JoinCompanyRequest.builder()
            .loginId(loginId)
            .password(password)
            .phone(phone)
            .role(role)
            .privacyConsent(privacyConsent)
            .deviceToken(deviceToken)
            .isNotification(isNotification)
            .businessNumber(businessNumber)
            .region(region)
            .companyName(companyName)
            .email(email)
            .manager(manager)
            .requestContent(requestContent)
            .build();
    }
}
