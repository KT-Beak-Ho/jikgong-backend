package jikgong.global.alimTalk;

import jikgong.global.alimTalk.service.AlimTalkService;
import jikgong.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlimTalkTestController {
    private final AlimTalkService alimTalkService;
    @GetMapping("/test")
    ResponseEntity<Response> test() {
        alimTalkService.sendAlimTalk("01031725949", "test", "test");
        return ResponseEntity.ok(new Response("testOk"));
    }
}
