package jikgong.domain.location.entity;

import jakarta.persistence.*;
import jikgong.domain.common.Address;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Location extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "location_id")
    private Long id;

    @Embedded
    private Address address;
    private Boolean isMain; // 대표 위치 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Location(Address address, Boolean isMain, Member member) {
        this.address = address;
        this.isMain = isMain;
        this.member = member;
    }

    public void changeMainLocation(Boolean main) {
        isMain = main;
    }
}
