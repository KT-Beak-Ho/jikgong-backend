//package jikgong.global.queryCount;
//
//import org.hibernate.resource.jdbc.spi.StatementInspector;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//
//import java.util.Objects;
//
//@Component
//public class ApiQueryInspector implements StatementInspector {
//
//    private final ApiQueryCounter apiQueryCounter;
//
//    public ApiQueryInspector(final ApiQueryCounter apiQueryCounter) {
//        this.apiQueryCounter = apiQueryCounter;
//    }
//
//    @Override
//    public String inspect(final String sql) {
//        if (isInRequestScope()) {
//            // 쿼리 카운터를 증가
//            apiQueryCounter.increaseCount();
//        }
//        return sql;
//    }
//
//    private boolean isInRequestScope() {
//        // RequestContextHolder를 사용하여 현재 요청 속성이 있는지 확인
//        return Objects.nonNull(RequestContextHolder.getRequestAttributes());
//    }
//}