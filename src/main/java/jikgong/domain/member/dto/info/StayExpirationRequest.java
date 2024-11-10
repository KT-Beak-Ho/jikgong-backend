package jikgong.domain.member.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StayExpirationRequest {

    @Schema(description = "여권 번호", example = "G60123456")
    private String passportNo;
    @Schema(description = "국적 코드 / 0:러시아, 1:몽골, 2:미국, 3:베트남, 4:인도, 5:인도네시아, 6:일본, 7:중국, 8:타이, 9:필리핀, 10:한국계 러시아인, 11:한국계 중국인, 99:기타", example = "9")
    private String nationality;  // 국적 코드
    @Schema(description = "국가명 / nationality 99 일때만 필수", example = "우주베키스탄")
    private String country;      // nationality가 99일 때 필수
    @Schema(description = "YYYYMMDD", example = "19910101")
    private String birthDate;    // YYYYMMDD 형식의 생년월일
}