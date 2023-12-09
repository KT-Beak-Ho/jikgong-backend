package jikgong.domain.location.entity;

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
public class Location extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "location_id")
    private Long id;

    private Float latitude; // 위도
    private Float longitude; // 경도
    private String address; // 주소
    private Boolean isMain; // 대표 위치 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Location(Float latitude, Float longitude, String address, Boolean isMain, Member member) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.isMain = isMain;
        this.member = member;
    }

    public void changeMainLocation(Boolean main) {
        isMain = main;
    }
}
