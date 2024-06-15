//package jikgong.global.queryCount;
//
//import lombok.RequiredArgsConstructor;
//import org.hibernate.cfg.AvailableSettings;
//import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class HibernateConfig {
//
//    private final ApiQueryInspector apiQueryInspector;
//
//    @Bean
//    public HibernatePropertiesCustomizer hibernatePropertyConfig() {
//        return hibernateProperties ->
//                hibernateProperties.put(AvailableSettings.STATEMENT_INSPECTOR, apiQueryInspector);
//    }
//}