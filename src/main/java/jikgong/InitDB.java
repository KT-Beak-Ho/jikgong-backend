//package jikgong;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import jikgong.domain.common.Address;
//import jikgong.domain.location.entity.Location;
//import jikgong.domain.member.entity.*;
//import jikgong.domain.project.entity.Project;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.password.PasswordEncoder;
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
//
//    @Service
//    @RequiredArgsConstructor
//    @Transactional
//    public static class InitService {
//        private final EntityManager em;
//        private final PasswordEncoder encoder;
//
//        public void initMember() {
//            Company company = getCompany();
//            Member member1 = createMember("01012345678", null, company, Role.ROLE_COMPANY);
//            Address address1 = new Address("부산광역시 사하구 낙동대로 550번길 37", 35.116777388697734F, 128.9685393114043F);
//            Location location1 = getLocation(member1, address1);
//            em.persist(member1);
//            em.persist(location1);
//
//            Worker worker2 = getWorker("이승민");
//            Member member2 = createMember("01011111111", worker2, null, Role.ROLE_WORKER);
//            Address address2 = new Address("부산광역시 사하구 낙동대로 550번길 11", 35.116777388697734F, 129.9685393114043F);
//            Location location2 = getLocation(member2, address2);
//            em.persist(member2);
//            em.persist(location2);
//
//            Worker worker3 = getWorker("조영훈");
//            Member member3 = createMember("01022222222", worker3, null, Role.ROLE_WORKER);
//            Address address3 = new Address("부산광역시 사하구 낙동대로 550번길 13", 35.116777388697734F, 128.0685393114043F);
//            Location location3 = getLocation(member3, address3);
//            em.persist(member3);
//            em.persist(location3);
//
//
//            Address address4 = new Address("부산광역시 사하구 낙동대로 550번길 37", 35.116777388697734F, 128.9685393114043F);
//            Project project = Project.builder()
//                    .name("사하구 낙동5블럭 낙동강 온도 측정 센터 신축공사")
//                    .startDate(LocalDate.of(2024, 01, 01))
//                    .endDate(LocalDate.of(2024, 04, 01))
//                    .address(address4)
//                    .member(member1)
//                    .build();
//            em.persist(project);
//
//        }
//
//        private static Location getLocation(Member member, Address address) {
//            Location location = Location.builder()
//                    .address(address)
//                    .isMain(true)
//                    .member(member)
//                    .build();
//            return location;
//        }
//
//        private static Worker getWorker(String name) {
//            Worker worker = Worker.builder()
//                    .workerName(name)
//                    .birth("20000930")
//                    .rrn("000930-31111111")
//                    .gender(Gender.MALE)
//                    .nationality("한국")
//                    .build();
//            return worker;
//        }
//
//        private static Company getCompany() {
//            Company company = Company.builder()
//                    .businessNumber("1234")
//                    .region("부산")
//                    .companyName("직공회사")
//                    .email("rlawlstn@gmail.com")
//                    .manager("김진수")
//                    .requestContent("문의내용")
//                    .build();
//            return company;
//        }
//
//        private Member createMember(String phone, Worker worker, Company company, Role role) {
//            // 공통 부분
//            Member member = Member.builder()
//                    .phone(phone)
//                    .authCode(encoder.encode("123456"))
//                    .account("12341234123412")
//                    .bank("국민은행")
//                    .role(role)
//                    .deviceToken("deviceToken")
//                    .isNotification(true)
//                    .workerInfo(worker)
//                    .companyInfo(company)
//                    .build();
//            return member;
//        }
//    }
//}
