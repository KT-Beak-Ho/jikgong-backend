package jikgong.domain.location.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LocationSearchResponse {
    private String road_address; // 도로명 주소
    private String address; // 지번 주소
    private Float latitude; // 위도
    private Float longitude; // 경도
}
