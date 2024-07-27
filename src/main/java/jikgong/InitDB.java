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
import jikgong.domain.jobpost.entity.Park;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.jobpost.service.JobPostCompanyService;
import jikgong.domain.member.dto.join.JoinCompanyRequest;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Role;
import jikgong.domain.member.service.LoginService;
import jikgong.domain.project.dto.ProjectSaveRequest;
import jikgong.domain.project.service.ProjectService;
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

        private final LoginService loginService;
        private final ProjectService projectService;
        private final JobPostCompanyService jobPostCompanyService;

        public void initMember() {
            Long savedId = loginService.joinCompanyMember(
                createJoinCompanyRequest("abcdefg0", "01012345678", Role.ROLE_COMPANY));

            loginService.joinWorkerMember(
                createJoinWorkerRequest("abcdefg1", "01011111111", Role.ROLE_WORKER, "111122223333", "김진수",
                    "2000-09-30", "000930",
                    35.116777388697734F, 128.9685393114043F));
            loginService.joinWorkerMember(
                createJoinWorkerRequest("abcdefg2", "01022222222", Role.ROLE_WORKER, "222233334444", "이승민",
                    "2001-02-12", "010212",
                    35.086777388697734F, 128.9585393114043F));
            loginService.joinWorkerMember(
                createJoinWorkerRequest("abcdefg3", "01033333333", Role.ROLE_WORKER, "333344445555", "조영훈",
                    "2002-07-20", "020720",
                    35.092233188697734F, 128.9485393114043F));
            loginService.joinWorkerMember(
                createJoinWorkerRequest("abcdefg4", "01044444444", Role.ROLE_WORKER, "444455556666", "안병기",
                    "2002-12-04", "021204",
                    35.142157388697734F, 128.9385393114043F));

            projectService.saveProject(savedId, createProjectSaveRequest());

            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("부산 남구 우암1구역 주택재개발", 120000, Park.FREE, true, true, "부산 남구 우암로 168-1",
                    35.11607388697734F, 128.9605373114043F), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("부산 강서구 명지동 빌리브 명시 듀클래스", 150000, Park.FREE, false, true, "부산 강서구 명지동 3605-6",
                    35.11607388697734F, 128.9605393134043F), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("역세권 청년주택 신축공사", 110000, Park.NONE, true, true, "서울 중랑구 상봉동",
                    35.12607388697734F, 128.9625393114043F), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("분당 느티마을 4단지 리모델링 현장", 90000, Park.FREE, true, true, "경기 성남시 분당 느티마을 4단지",
                    35.11607388697734F, 128.9605393134043F), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("경기 의왕시 초평 스마트시티퀀텀", 130000, Park.PAID, true, true, "경기 의왕시",
                    35.13607388697734F, 128.8605393114043F), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("서울 강남구 근린생활시설 리노베이션 공사", 80000, Park.FREE, true, false, "서울 강남구 상섬동 130-5",
                    35.11607318697734F, 128.9605393114043F), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("서울 서초구 GDC&Office 신축공사", 88000, Park.PAID, true, true, "서울 서초구",
                    35.11607388697734F, 128.9608393114043F), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("서울 강서구 염창동 등촌역 지식산업센터 개발사업", 145000, Park.FREE, true, true, "서울 강서구 염창동 등촌역",
                    35.11607288697734F, 128.9605393114043F), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("서울 중구 필동1가 3-1 업무시설 신축공사", 150000, Park.NONE, true, false, "서울 중구 필동1가 3-1",
                    35.11607348697734F, 128.9605393114043F), null);
            jobPostCompanyService.saveJobPost(savedId,
                createJobPostSaveRequest("서울 서대문구 창천동 청년주택 신축공사", 117000, Park.PAID, false, true, "서울 서대문구 창천동",
                    35.12607388697734F, 128.9305393114043F), null);
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

        private JoinWorkerRequest createJoinWorkerRequest(String loginId, String phone, Role role, String account,
            String workerName,
            String birth, String rrn, Float latitude, Float longitude) {
            return JoinWorkerRequest.builder()
                .loginId(loginId)
                .password("abcdefg1")
                .phone(phone)
                .role(role)
                .account(account)
                .bank("국민은행")
                .deviceToken("deviceToken")
                .isNotification(true)
                .workerName(workerName)
                .birth(birth)
                .rrn(rrn)
                .gender(Gender.MALE)
                .nationality("대한민국")
                .address("부산광역시 사하구 낙동대로 550번길 37")
                .latitude(latitude)
                .longitude(longitude)
                .build();
        }

        private JoinCompanyRequest createJoinCompanyRequest(String loginId, String phone, Role role) {
            return JoinCompanyRequest.builder()
                .loginId(loginId)
                .password("abcdefg1")
                .phone(phone)
                .role(role)
                .account("12341234123412")
                .bank("국민은행")
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
            boolean pickup, String address, Float latitude, Float longitude) {
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
                .dateList(generateRandomDates(3))
                .pickupList(Arrays.asList(
                    "부산광역시 사하구 낙동대로 550번길 37",
                    "대한민국 부산광역시 서구 구덕로 225"
                ))
                .managerName("홍길동")
                .phone("01012345678")
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
