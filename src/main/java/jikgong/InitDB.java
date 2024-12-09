/**
 * 해당 코드는 개발 단계에서 Dummy Data를 삽입하기 위한 코드입니다.
 * initService.initMember(); 호출 부분을 항상 주석 처리해주시고
 * 사용할 때만 주석을 풀어 사용해주세요
 */

package jikgong;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import jikgong.domain.jobpost.dto.company.JobPostSaveRequest;
import jikgong.domain.jobpost.entity.jobpost.Park;
import jikgong.domain.jobpost.service.JobPostCompanyService;
import jikgong.domain.member.dto.join.JoinCompanyRequest;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Nationality;
import jikgong.domain.member.entity.Role;
import jikgong.domain.member.service.JoinService;
import jikgong.domain.project.dto.ProjectSaveRequest;
import jikgong.domain.project.service.ProjectService;
import jikgong.domain.workexperience.dto.WorkExperienceRequest;
import jikgong.domain.workexperience.entity.Tech;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init() {
//        initService.initMember();
    }

    @Service
    @RequiredArgsConstructor
    @Transactional
    public static class InitService {

        private final JoinService joinService;
        private final ProjectService projectService;
        private final JobPostCompanyService jobPostCompanyService;

        public void initMember() {
            Long savedId = joinService.joinCompanyMember(
                createJoinCompanyRequest("abcdefg0", "01012345678", Role.ROLE_COMPANY));

            joinService.joinWorkerMember(
                createJoinWorkerRequest("abcdefg1", "01011111111", "email1@naver.com", Role.ROLE_WORKER, "111122223333",
                    "김진수",
                    "2000-09-30", "000930",
                    35.116777388697734F, 128.9685393114043F));
            joinService.joinWorkerMember(
                createJoinWorkerRequest("abcdefg2", "01022222222", "email2@naver.com", Role.ROLE_WORKER, "222233334444",
                    "이승민",
                    "2001-02-12", "010212",
                    35.086777388697734F, 128.9585393114043F));
            joinService.joinWorkerMember(
                createJoinWorkerRequest("abcdefg3", "01033333333", "email3@naver.com", Role.ROLE_WORKER, "333344445555",
                    "조영훈",
                    "2002-07-20", "020720",
                    35.092233188697734F, 128.9485393114043F));
            joinService.joinWorkerMember(
                createJoinWorkerRequest("abcdefg4", "01044444444", "email4@naver.com", Role.ROLE_WORKER, "444455556666",
                    "안병기",
                    "2002-12-04", "021204",
                    35.142157388697734F, 128.9385393114043F));

            projectService.saveProject(savedId, createProjectSaveRequest());

            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("부산 남구 우암1구역 주택재개발", 120000, Park.FREE, true, true, "부산 남구 우암로 168-1",
                    35.11607388697734F, 128.9605373114043F, "부산시", "남구"), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("부산 강서구 명지동 빌리브 명시 듀클래스", 150000, Park.FREE, false, true, "부산 강서구 명지동 3605-6",
                    35.11607388697734F, 128.9605393134043F, "부산시", "강서구"), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("역세권 청년주택 신축공사", 110000, Park.NONE, true, true, "서울 중랑구 상봉동",
                    35.12607388697734F, 128.9625393114043F, "서울시", "중량구"), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("분당 느티마을 4단지 리모델링 현장", 90000, Park.FREE, true, true, "경기 성남시 분당 느티마을 4단지",
                    35.11607388697734F, 128.9605393134043F, "성남시", "분당구"), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("경기 의왕시 초평 스마트시티퀀텀", 130000, Park.PAID, true, true, "경기 의왕시",
                    35.13607388697734F, 128.8605393114043F, "의왕시", null), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("서울 강남구 근린생활시설 리노베이션 공사", 80000, Park.FREE, true, false, "서울 강남구 상섬동 130-5",
                    35.11607318697734F, 128.9605393114043F, "서울시", "강남구"), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("서울 서초구 GDC&Office 신축공사", 88000, Park.PAID, true, true, "서울 서초구",
                    35.11607388697734F, 128.9608393114043F, "서울시", "서초구"), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("서울 강서구 염창동 등촌역 지식산업센터 개발사업", 145000, Park.FREE, true, true, "서울 강서구 염창동 등촌역",
                    35.11607288697734F, 128.9605393114043F, "서울시", "강서구"), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("서울 중구 필동1가 3-1 업무시설 신축공사", 150000, Park.NONE, true, false, "서울 중구 필동1가 3-1",
                    35.11607348697734F, 128.9605393114043F, "서울시", "중구"), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("서울 서대문구 창천동 청년주택 신축공사", 117000, Park.PAID, false, true, "서울 서대문구 창천동",
                    35.12607388697734F, 128.9305393114043F, "서울시", "서대문구"), null);
        }

        private ProjectSaveRequest createProjectSaveRequest() {
            return ProjectSaveRequest.builder()
                .projectName("사하구 낙동5블럭 낙동강 온도 측정 센터 신축공사")
                .startDate(LocalDate.of(2024, 7, 1))
                .endDate(LocalDate.of(2024, 12, 1))
                .address("부산광역시 사하구 낙동대로 550번길 37")
                .latitude(35.116777388697734F)
                .longitude(128.9685393114043F)
                .build();
        }

        private JoinWorkerRequest createJoinWorkerRequest(String loginId, String phone, String email, Role role,
            String account,
            String workerName,
            String birth, String rrn, Float latitude, Float longitude) {

            // 랜덤한 2개의 경력 사항 생성
            List<WorkExperienceRequest> workExperienceRequestList = createTechList();

            return JoinWorkerRequest.builder()
                .loginId(loginId)
                .password("abcdefg1")
                .phone(phone)
                .email(email)
                .role(role)
                .account(account)
                .bank("국민은행")
                .privacyConsent(true)
                .deviceToken("deviceToken")
                .isNotification(true)
                .workerName(workerName)
                .birth(birth)
                .rrn(rrn)
                .gender(Gender.MALE)
                .nationality(Nationality.KOREAN)
                .hasVisa(true)
                .hasEducationCertificate(true)
                .hasWorkerCard(true)
                .credentialLiabilityConsent(true)
                .workExperienceRequest(workExperienceRequestList)
                .address("부산광역시 사하구 낙동대로 550번길 37")
                .latitude(latitude)
                .longitude(longitude)
                .build();
        }

        private List<WorkExperienceRequest> createTechList() {
            // 랜덤 객체 생성
            Random random = new Random();

            // Tech enum에서 랜덤으로 하나 선택
            Tech[] techValues = Tech.values();

            // 첫 번째 WorkExperienceRequest 생성
            Tech randomTech1 = techValues[random.nextInt(techValues.length)];
            int randomMonths1 = random.nextInt(49) + 12; // 12 ~ 60개월 사이의 값
            WorkExperienceRequest workExperienceRequest1 = WorkExperienceRequest.builder()
                .tech(randomTech1)
                .experienceMonths(randomMonths1)
                .build();

            // 두 번째 WorkExperienceRequest 생성
            Tech randomTech2 = techValues[random.nextInt(techValues.length)];
            int randomMonths2 = random.nextInt(49) + 12; // 12 ~ 60개월 사이의 값
            WorkExperienceRequest workExperienceRequest2 = WorkExperienceRequest.builder()
                .tech(randomTech2)
                .experienceMonths(randomMonths2)
                .build();

            // 두 객체를 리스트에 추가하고 반환
            return Arrays.asList(workExperienceRequest1, workExperienceRequest2);
        }

        private JoinCompanyRequest createJoinCompanyRequest(String loginId, String phone, Role role) {
            return JoinCompanyRequest.builder()
                .loginId(loginId)
                .password("abcdefg1")
                .phone(phone)
                .role(role)
                .account("12341234123412")
                .bank("국민은행")
                .privacyConsent(true)
                .deviceToken("deviceToken")
                .isNotification(true)
                .businessNumber("0000000")
                .region("서울")
                .companyName("삼성")
                .email("jaeyoung@naver.com")
                .manager("이재용")
                .requestContent("직공 서비스에 가입하고 싶습니다.")
                .build();
        }

        private JobPostSaveRequest createJobPostSaveRequest(String title, Integer wage, Park park, boolean meal,
            boolean pickup, String address, Float latitude, Float longitude, String city, String district) {
            return JobPostSaveRequest.builder()
                .title(title)
                .tech(Tech.NORMAL)
                .startTime(LocalTime.of(9, 30))
                .endTime(LocalTime.of(18, 0))
                .recruitNum(60)
                .wage(wage)
                .preparation("작업복, 작업화")
                .parkDetail("2번 GateWay 옆 공간")
                .meal(meal)
                .pickup(pickup)
                .park(park)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .city(city)
                .district(district)
                .dateList(generateRandomDates(3))
                .pickupList(Arrays.asList(
                    "부산광역시 사하구 낙동대로 550번길 37",
                    "대한민국 부산광역시 서구 구덕로 225"
                ))
                .managerName("홍길동")
                .phone("01012345678")
                .description("""
                    주요업무
                    (업무내용)
                    - 건축구조물의 내·외벽, 바닥, 천장 등에 각종 장비를 사용해 타일을 시멘트 또는 기타 접착제로 붙여서 마감
                    - 주택, 상업시설, 문화시설 등의 고품질화
                    - 외벽, 바닥, 천정 등에 각종 도기류 및 화학 제품류의 타일을 접착
                    """)
                .projectId(1L)
                .build();
        }

        private List<LocalDate> generateRandomDates(int count) {
            Random random = new Random();
            return IntStream.range(0, count)
                .mapToObj(i -> LocalDate.of(2024, 10, random.nextInt(30) + 1))
                .collect(Collectors.toList());
        }
    }
}
