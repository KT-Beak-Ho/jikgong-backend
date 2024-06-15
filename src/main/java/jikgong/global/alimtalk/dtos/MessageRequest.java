package jikgong.global.alimtalk.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MessageRequest {
    String to;
    String content;
}
