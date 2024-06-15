package jikgong.domain.location.entity;

import jakarta.persistence.*;
import jikgong.domain.common.Address;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.location.dto.LocationUpdateRequest;
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

    // 대표 위치 변경
    public void changeMainLocation(Boolean main) {
        this.isMain = main;
    }

    // 위치 정보 업데이트
    public void updateLocation(LocationUpdateRequest request) {
        this.address.setAddress(request.getAddress());
        this.address.setLatitude(request.getLatitude());
        this.address.setLongitude(request.getLongitude());
    }
}
