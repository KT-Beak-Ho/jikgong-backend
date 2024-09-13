package jikgong.domain.visaimage.dto;

import jikgong.domain.visaimage.entity.VisaImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VisaImageResponse {

    private String s3Url;

    public static VisaImageResponse from(VisaImage visaImage) {
        return VisaImageResponse.builder()
            .s3Url(visaImage.getS3Url())
            .build();
    }
}
