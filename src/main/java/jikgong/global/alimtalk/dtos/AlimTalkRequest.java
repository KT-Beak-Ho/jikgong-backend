package jikgong.global.alimtalk.dtos;

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
    /**
     * sens 서버에 보내기 위한 dto
     */
    private String plusFriendId;
    private String templateCode;
    private List<MessageRequest> messages;
}
