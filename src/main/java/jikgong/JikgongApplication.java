package jikgong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients // feign client
@EnableJpaAuditing // auditing
@EnableAspectJAutoProxy // aop
public class JikgongApplication {

	public static void main(String[] args) {
		SpringApplication.run(JikgongApplication.class, args);
	}

}
