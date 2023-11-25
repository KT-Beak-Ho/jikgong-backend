package jikgong.global.alimTalk.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AlimTalkRequest {
    private String plusFriendId;
    private String templateCode;
    private List<MessageRequest> messages;
}
