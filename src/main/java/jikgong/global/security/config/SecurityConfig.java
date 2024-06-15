package jikgong.global.security.config;

import jikgong.global.security.exceptionHandler.AccessDeniedHandlerImpl;
import jikgong.global.security.exceptionHandler.AuthenticationEntryPointHandlerImpl;
import jikgong.global.security.filter.JwtFilter;
import jikgong.global.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final AccessDeniedHandlerImpl accessDeniedHandler;
    private final AuthenticationEntryPointHandlerImpl authenticationEntryPointHandler;
    private final JwtFilter jwtFilter;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .cors() // cors 커스텀 설정
            .and()

            .sessionManagement()//세션 사용 x
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            .authorizeHttpRequests()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
            .requestMatchers("/api/project/**", "/api/notification/company/**", "/api/job-post/company/**",
                "/api/offer/company/**", "/api/resume/company/**", "/api/apply/company/**", "/api/history/**",
                "/api/like/**").hasRole("COMPANY")
            .requestMatchers("/api/profit/**", "/api/notification/worker/**", "/api/offer/worker/**", "/api/scrap/**",
                "/api/resume/worker/**", "/api/apply/worker/**", "/api/certification/worker/**", "/api/location/**")
            .hasRole("WORKER")
            .requestMatchers("/api/notification/**", "/api/certification/**", "/api/searchLog/**")
            .hasAnyRole("COMPANY", "WORKER")
            .requestMatchers("/api/job-post/worker/**", "/api/login/**", "/api/join/**", "/api/member/search/**")
            .permitAll()
            .anyRequest().permitAll()
        ;

        http
            .userDetailsService(customUserDetailsService);

        http
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

//        http.exceptionHandling()
//                .accessDeniedHandler(accessDeniedHandler) // 커스텀 AccessDeniedHandler 등록
//                .authenticationEntryPoint(authenticationEntryPointHandler); // 커스텀 AuthenticationEntryPoint 등록

        return http.build();
    }
}
