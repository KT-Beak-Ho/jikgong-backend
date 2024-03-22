package jikgong.domain.offer.dtos.worker;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class OfferProcessRequest {
    @Schema(description = "offerWorkDateId", example = "1")
    private Long offerWorkDateId;
    @Schema(description = "수락 여부", example = "true")
    private Boolean isAccept; // 수락 여부
}
