package jikgong.domain.headHunting.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.jobPost.entity.AvailableInfo;
import jikgong.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HeadHunting extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "head_hunting_id")
    private Long id;

    @Embedded
    private AvailableInfo availableInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
