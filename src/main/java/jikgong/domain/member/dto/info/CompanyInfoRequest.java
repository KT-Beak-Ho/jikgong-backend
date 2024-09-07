package jikgong.domain.member.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CompanyInfoRequest {

    @Schema(description = "사업자 번호", example = "11111111")
    private String businessNumber; // 사업자 번호
    @Schema(description = "지역", example = "부산광역시")
    private String region; // 지역
    @Schema(description = "회사 명", example = "현대 건설")
    private String companyName; // 회사 명
    @Schema(description = "이메일", example = "hyundai@naver.com")
    private String email; // 이메일
    @Schema(description = "담당자 이름", example = "정주일")
    private String manager; // 담당자 이름
}
