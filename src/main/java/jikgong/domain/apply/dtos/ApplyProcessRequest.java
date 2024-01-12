package jikgong.domain.apply.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ApplyProcessRequest {
    @Schema(description = "jobPostId", example = "1")
    private Long jobPostId;
    @Schema(description = "수락할 노동자 Id 리스트", example = "[\"1\", \"2\"]")
    private List<Long> targetMemberIdList;
    @Schema(description = "수락(true) or 거절(false)", example = "true")
    private Boolean isAccept;
}
