package jikgong.domain.certification.dto;

import jikgong.domain.certification.entity.Certification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CertificationResponse {

    private Long certificationId;
    private String s3Url; // s3 url

    public static CertificationResponse from(Certification certification) {
        return CertificationResponse.builder()
            .certificationId(certification.getId())
            .s3Url(certification.getS3Url())
            .build();
    }
}
