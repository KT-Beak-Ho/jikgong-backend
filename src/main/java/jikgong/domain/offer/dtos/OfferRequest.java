package jikgong.domain.offer.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class OfferRequest {
    @Schema(description = "resumeId", example = "1")
    private Long resumeId;

    private List<OfferJobPostRequest> offerJobPostRequest;

}
