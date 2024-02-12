package jikgong.domain.headHunting.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OfferRequest {
    @Schema(description = "memberId", example = "1")
    private Long memberId;
    @Schema(description = "jobPostId", example = "1")
    private Long jobPostId;

}
