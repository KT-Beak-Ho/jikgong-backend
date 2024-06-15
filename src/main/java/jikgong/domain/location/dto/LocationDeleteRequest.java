package jikgong.domain.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class LocationDeleteRequest {

    @Schema(description = "삭제할 Location Id list", example = "[\"1\", \"2\"]")
    private List<Long> locationIdList;
}
