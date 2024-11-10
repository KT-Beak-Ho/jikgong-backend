package jikgong.global.config;

import com.google.firebase.messaging.FirebaseMessaging;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")  // test 프로파일에서만 활성화
public class MockFcmConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        // 테스트에서 사용할 더미 FirebaseMessaging 객체 반환
        return Mockito.mock(FirebaseMessaging.class);
    }
}
