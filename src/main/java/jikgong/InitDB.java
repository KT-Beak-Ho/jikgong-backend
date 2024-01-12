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
//            Worker worker = Worker.builder()
//                    .workerName("김진수")
//                    .birth("20000930")
//                    .rrn("000930-31111111")
//                    .gender(Gender.MALE)
//                    .nationality("한국")
//                    .build();
//
//            // 공통 부분
//            Member member = Member.builder()
//                    .phone("01011111111")
//                    .authCode(encoder.encode("123456"))
//                    .account("12341234123412")
//                    .bank("국민은행")
//                    .role(Role.ROLE_WORKER)
//                    .deviceToken("deviceToken")
//                    .isNotification(true)
//                    .workerInfo(worker)
//                    .build();
//
//            Location location = Location.builder()
//                    .address(new Address("부산광역시 사하구 낙동대로 550번길 37", 35.116777388697734F, 128.9685393114043F))
//                    .isMain(true)
//                    .member(member)
//                    .build();
//
//            em.persist(member);
//            em.persist(location);
//        }
//    }
//}
