package jikgong.global.alimTalk.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MessageRequest {
    String to;
    String content;
}
