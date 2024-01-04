package jikgong.domain.location.dtos;

import jikgong.domain.location.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
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
