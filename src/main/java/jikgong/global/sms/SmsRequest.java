package jikgong.global.sms;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsRequest {

    private String type;
    private String contentType; // 메시지 Type
    private String countryCode; // 국가 번호
    private String from; // 발신자, 사전 등록 번호만 가능
    private String subject; // LMS or MMS or null
    private String content; // 기본 메시지 내용
    private List<Message> messages;
    private List<File> files; // MMS 에서 사용 가능, 파일 업로드
    private String reserveTime; // 예약 일시
    private String reserveTimeZone; // 예약 일시 타임존

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {

        private String to; // 수신 번호
        private String subject; // LMS or MMS or null
        private String content; // 개별 메시지 내용
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class File {

        private String fileId;
    }
}