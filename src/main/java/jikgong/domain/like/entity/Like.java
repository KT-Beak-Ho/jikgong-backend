package jikgong.domain.like.entity;

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
@Table(name = "likes")
public class Like extends BaseEntity {
    /**
     * 회사가 노동자에게 누르는 좋아요
     */
    @Id @GeneratedValue
    @Column(name = "like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender; // 좋아요 누른 회원

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver; // 좋아요 받은 회원

    @Builder
    public Like(Member sender, Member receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
}
