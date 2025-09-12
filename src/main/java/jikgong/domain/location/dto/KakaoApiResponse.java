package jikgong.domain.location.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KakaoApiResponse {
    private List<DocumentDto> documents;

    @Getter
    @Setter
    public static class DocumentDto {
        @JsonProperty("address_name")
        private String lotNumberAddressName;

        @JsonProperty("road_address_name")
        private String roadAddressName;

        @JsonProperty("x")
        private Float longitude;

        @JsonProperty("y")
        private Float latitude;
    }
}
