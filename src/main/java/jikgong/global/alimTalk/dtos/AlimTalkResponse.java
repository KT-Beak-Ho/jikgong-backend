package jikgong.global.alimTalk.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlimTalkResponse {
    String requestId;
    LocalDateTime requestTime;
    String statusCode;
    String statusName;
}