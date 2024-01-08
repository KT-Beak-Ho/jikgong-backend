package jikgong.domain.addressInfo.entity;

import lombok.Getter;

@Getter
public enum AddressType {
    PARK("주차장"),
    PICK_UP("픽업 장소");

    private final String description;

    AddressType(String description) {
        this.description = description;
    }
}
