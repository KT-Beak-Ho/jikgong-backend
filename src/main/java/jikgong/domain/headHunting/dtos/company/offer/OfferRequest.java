package jikgong.domain.headHunting.dtos.company.offer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class OfferRequest {
    @Schema(description = "headHuntingId", example = "1")
    private Long headHuntingId;

    private List<OfferJobPostRequest> offerJobPostRequest;

}
