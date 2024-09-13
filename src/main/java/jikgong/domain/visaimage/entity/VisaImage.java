package jikgong.domain.visaimage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jikgong.global.s3.ImageDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VisaImage {

    @Id
    @GeneratedValue
    @Column(name = "visa_image_id")
    private Long id;

    private String storeImgName; // 저장된 이미지 명 (uuid)
    private String s3Url; // s3 url

    @Builder
    public VisaImage(String storeImgName, String s3Url) {
        this.storeImgName = storeImgName;
        this.s3Url = s3Url;
    }

    public static VisaImage createEntity(ImageDto imageDto) {
        return VisaImage.builder()
            .storeImgName(imageDto.getStoreImgName())
            .s3Url(imageDto.getS3Url())
            .build();
    }
}
