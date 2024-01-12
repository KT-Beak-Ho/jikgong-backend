package jikgong.domain.apply.dtos;

import io.netty.channel.ChannelHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class AcceptedMemberRequest {
    @Schema(description = "jobPostId", example = "1")
    private Long jobPostId;
    @Schema(description = "선택 날짜", example = "2024-01-01")
    private LocalDate workDate;
}
