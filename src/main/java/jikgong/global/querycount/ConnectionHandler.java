package jikgong.global.querycount;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class ConnectionHandler implements InvocationHandler {
    private static final String QUERY_PREPARE_STATEMENT = "prepareStatement";

    private final Object connection;
    private final QueryCounter queryCounter;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        countQuery(method);
        return method.invoke(connection, args);
    }

    private void countQuery(Method method) {
        if (isPrepareStatement(method) && isRequest()) {
            queryCounter.increase();
        }
    }

    private boolean isPrepareStatement(Method method) {
        return method.getName().equals(QUERY_PREPARE_STATEMENT);
    }

    private boolean isRequest() {
        return RequestContextHolder.getRequestAttributes() != null;
    }
}
