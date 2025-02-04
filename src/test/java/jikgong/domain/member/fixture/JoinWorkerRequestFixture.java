package jikgong.domain.member.fixture;

import java.util.Collections;
import java.util.List;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Nationality;
import jikgong.domain.member.entity.Role;
import jikgong.domain.workexperience.dto.WorkExperienceRequest;
import jikgong.domain.workexperience.entity.Tech;

public class JoinWorkerRequestFixture {

    public static JoinWorkerRequest createDefault() {
        return JoinWorkerRequest.builder()
            .loginId("worker1")
            .password("password123")
            .phone("01012345678")
            .email("email@naver.com")
            .role(Role.ROLE_WORKER)
            .privacyConsent(true)
            .deviceToken("sampleToken")
            .isNotification(true)
            .workerName("홍길동")
            .birth("19750101")
            .gender(Gender.MALE)
            .nationality(Nationality.KOREAN)
            .account("12341234123412")
            .bank("국민은행")
            .workerCardNumber("123456789")
            .hasVisa(false)
            .credentialLiabilityConsent(true)
            .workExperienceRequest(createDefaultWorkExperience())
            .address("부산광역시 사하구 낙동대로 550번길 37")
            .latitude(35.116777f)
            .longitude(128.968539f)
            .build();
    }

    private static List<WorkExperienceRequest> createDefaultWorkExperience() {
        return Collections.singletonList(
            new WorkExperienceRequest(null, Tech.NORMAL, 24)
        );
    }
}