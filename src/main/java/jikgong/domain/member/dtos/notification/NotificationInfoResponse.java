package jikgong.domain.member.dtos.notification;

import jikgong.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class NotificationInfoResponse {
    private Boolean isNotification;

    public static NotificationInfoResponse from(Member member) {
        return NotificationInfoResponse.builder()
                .isNotification(member.getIsNotification())
                .build();
    }
}
