package jikgong.global.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class CoreFeignConfiguration {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
