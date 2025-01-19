package jikgong.domain.member.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import java.util.List;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Nationality;
import jikgong.domain.workexperience.dto.WorkExperienceRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class WorkerInfoRequest {

    @Schema(description = "이메일", example = "gildong@gmail.com")
    private String email; // 이메일
    @Schema(description = "노동자 이름", example = "김삿갓")
    private String workerName; // 노동자 이름
    @Schema(description = "생년월일", example = "20200930")
    private String birth; // 생년월일
    @Schema(description = "성별", example = "FEMALE")
    private Gender gender; // 성별
    @Schema(description = "국적", example = "FOREIGNER")
    private Nationality nationality; // 국적
    @Schema(description = "비자 여부", example = "true")
    private Boolean hasVisa; // 비자 여부

    // 경력 정보
    @Schema(description = "경력 정보 리스트", example = "[{ \"workExperienceId\": 1, \"tech\": \"NORMAL\", \"experienceMonths\": 36 }, { \"workExperienceId\": null, \"tech\": \"TILE\", \"experienceMonths\": 12 }]")
    private List<WorkExperienceRequest> workExperienceRequestList; // 경력 정보 리스트
}
