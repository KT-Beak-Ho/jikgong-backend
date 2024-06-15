package jikgong.global.querycount;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class QueryCounter {

    private int count;

    public void increase() {
        count++;
    }

    public boolean isWarn() {
        return count >= 10;
    }
}