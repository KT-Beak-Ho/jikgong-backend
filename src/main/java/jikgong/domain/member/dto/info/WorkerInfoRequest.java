package jikgong.domain.member.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Nationality;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class WorkerInfoRequest {

    @Schema(description = "노동자 이름", example = "김삿갓")
    private String workerName; // 노동자 이름
    @Schema(description = "생년월일", example = "20200930")
    private String birth; // 생년월일
    @Schema(description = "주민등록번호", example = "000930")
    private String rrn; // 주민등록번호
    @Schema(description = "성별", example = "FEMALE")
    private Gender gender; // 성별
    @Schema(description = "국적", example = "FOREIGNER")
    private Nationality nationality; // 국적
}
