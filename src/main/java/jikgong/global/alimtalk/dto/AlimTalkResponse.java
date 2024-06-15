package jikgong.global.alimtalk.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlimTalkResponse {
    /**
     * sens 서버로 부터 응답 받기 위한 dto
     */
    String requestId;
    LocalDateTime requestTime;
    String statusCode;
    String statusName;
}