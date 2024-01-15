//package jikgong;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import jikgong.domain.common.Address;
//import jikgong.domain.location.entity.Location;
//import jikgong.domain.member.entity.Gender;
//import jikgong.domain.member.entity.Member;
//import jikgong.domain.member.entity.Role;
//import jikgong.domain.member.entity.Worker;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
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
//            Worker worker1 = getWorker("김진수");
//            Member member1 = createMember("01012345678", worker1);
//            Location location1 = getLocation(member1);
//            em.persist(member1);
//            em.persist(location1);
//
//            Worker worker2 = getWorker("이승민");
//            Member member2 = createMember("01011111111", worker2);
//            Location location2 = getLocation(member2);
//            em.persist(member2);
//            em.persist(location2);
//
//            Worker worker3 = getWorker("조영훈");
//            Member member3 = createMember("01022222222", worker3);
//            Location location3 = getLocation(member3);
//            em.persist(member3);
//            em.persist(location3);
//        }
//
//        private static Location getLocation(Member member) {
//            Location location = Location.builder()
//                    .address(new Address("부산광역시 사하구 낙동대로 550번길 37", 35.116777388697734F, 128.9685393114043F))
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
//        private Member createMember(String phone, Worker worker) {
//            // 공통 부분
//            Member member = Member.builder()
//                    .phone(phone)
//                    .authCode(encoder.encode("123456"))
//                    .account("12341234123412")
//                    .bank("국민은행")
//                    .role(Role.ROLE_WORKER)
//                    .deviceToken("deviceToken")
//                    .isNotification(true)
//                    .workerInfo(worker)
//                    .build();
//            return member;
//        }
//    }
//}
