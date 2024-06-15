package jikgong.domain.location.dto;

import jikgong.domain.location.entity.Location;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LocationResponse {
    private Long locationId;
    private String address; // 도로명 주소
    private Boolean isMain; // 대표 위치 여부

    public static LocationResponse from(Location location) {
        return LocationResponse.builder()
                .locationId(location.getId())
                .address(location.getAddress().getAddress())
                .isMain(location.getIsMain())
                .build();
    }
}
