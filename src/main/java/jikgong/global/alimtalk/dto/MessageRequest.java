package jikgong.global.alimtalk.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MessageRequest {
    String to;
    String content;
}
