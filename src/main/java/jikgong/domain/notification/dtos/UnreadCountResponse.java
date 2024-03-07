package jikgong.domain.notification.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UnreadCountResponse {
    private Integer unreadCount;
}
