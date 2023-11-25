package jikgong.domain.pickupLocation.entity;

import jakarta.persistence.*;
import jikgong.domain.common.Address;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PickupLocation {

    @Id @GeneratedValue
    @Column(name = "pickup_location_id")
    private Long id;
    @Embedded
    private Address address; // 픽업 도로명 주소

    @Builder
    public PickupLocation(Address address) {
        this.address = address;
    }
}
