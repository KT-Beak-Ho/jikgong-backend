//package jikgong;
//
//import jakarta.annotation.PostConstruct;
//import jikgong.domain.member.dtos.join.JoinCompanyRequest;
//import jikgong.domain.member.dtos.join.JoinWorkerRequest;
//import jikgong.domain.member.entity.*;
//import jikgong.domain.member.service.LoginService;
//import jikgong.domain.project.dtos.ProjectSaveRequest;
//import jikgong.domain.project.service.ProjectService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class InitDB {
//    private final InitService initService;
//
//    @PostConstruct
//    public void init() {
//        initService.initMember();
//    }
//    @Service
//    @RequiredArgsConstructor
//    @Transactional
//    public static class InitService {
//        private final LoginService loginService;
//        private final ProjectService projectService;
//
//        public void initMember() {
//            Long savedId = loginService.joinCompanyMember(createJoinCompanyRequest("abcdefg0", "01012345678", Role.ROLE_COMPANY));
//
//            loginService.joinWorkerMember(createJoinWorkerRequest("abcdefg1", "01011111111", Role.ROLE_WORKER, "김진수", "2000-09-30", "000930", 35.116777388697734F, 128.9685393114043F));
//            loginService.joinWorkerMember(createJoinWorkerRequest("abcdefg2", "01022222222", Role.ROLE_WORKER, "이승민", "2001-02-12", "010212", 35.086777388697734F, 128.9585393114043F));
//            loginService.joinWorkerMember(createJoinWorkerRequest("abcdefg3", "01033333333", Role.ROLE_WORKER, "조영훈", "2002-07-20", "020720", 35.092233188697734F, 128.9485393114043F));
//            loginService.joinWorkerMember(createJoinWorkerRequest("abcdefg4", "01044444444", Role.ROLE_WORKER, "안병기", "2002-12-04", "021204", 35.142157388697734F, 128.9385393114043F));
//
//            projectService.saveProject(savedId, createProjectSaveRequest());
//        }
//
//        private ProjectSaveRequest createProjectSaveRequest() {
//            return ProjectSaveRequest.builder()
//                    .projectName("사하구 낙동5블럭 낙동강 온도 측정 센터 신축공사")
//                    .startDate(LocalDate.of(2024, 3, 1))
//                    .endDate(LocalDate.of(2024, 5, 1))
//                    .address("부산광역시 사하구 낙동대로 550번길 37")
//                    .latitude(35.116777388697734F)
//                    .longitude(128.9685393114043F)
//                    .build();
//        }
//
//        private JoinWorkerRequest createJoinWorkerRequest(String loginId, String phone, Role role, String workerName, String birth, String rrn, Float latitude, Float longitude) {
//            return JoinWorkerRequest.builder()
//                    .loginId(loginId)
//                    .password("abcdefg1")
//                    .phone(phone)
//                    .role(role)
//                    .account("12341234123412")
//                    .bank("국민은행")
//                    .deviceToken("deviceToken")
//                    .isNotification(true)
//                    .workerName(workerName)
//                    .birth(birth)
//                    .rrn(rrn)
//                    .gender(Gender.MALE)
//                    .nationality("대한민국")
//                    .address("부산광역시 사하구 낙동대로 550번길 37")
//                    .latitude(latitude)
//                    .longitude(longitude)
//                    .build();
//        }
//
//        private JoinCompanyRequest createJoinCompanyRequest(String loginId, String phone, Role role) {
//            return JoinCompanyRequest.builder()
//                    .loginId(loginId)
//                    .password("abcdefg1")
//                    .phone(phone)
//                    .role(role)
//                    .account("12341234123412")
//                    .bank("국민은행")
//                    .deviceToken("deviceToken")
//                    .isNotification(true)
//                    .businessNumber("0000000")
//                    .region("서울")
//                    .companyName("삼성")
//                    .email("jaeyoung@naver.com")
//                    .manager("이재용")
//                    .requestContent("직공 서비스에 가입하고 싶습니다.")
//                    .build();
//        }
//    }
//}
