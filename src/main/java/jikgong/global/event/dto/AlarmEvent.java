package jikgong.global.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlarmEvent {
    private String content;
    private Long receiverId;
    private String phone;
    private String templateCode;
}
