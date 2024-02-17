package jikgong.domain.notification.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    private String content;
    private String url;
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member receiver;

    @Builder
    public Notification(String content, String url, NotificationType notificationType, Member receiver) {
        this.content = content;
        this.url = url;
        this.isRead = false;
        this.notificationType = notificationType;
        this.receiver = receiver;
    }

    public void readNotification() {
        this.isRead = true;
    }
}
