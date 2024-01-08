package jikgong.domain.jobPostImage.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jikgong.global.handler.ImageDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class JobPostImage {
    @Id
    @GeneratedValue
    @Column(name = "job_post_image")
    private Long id;

    private String storeImgName; // 저장된 이미지 명 (uuid)
    private String s3Url; // s3 url

    @Builder
    public JobPostImage(String storeImgName, String s3Url) {
        this.storeImgName = storeImgName;
        this.s3Url = s3Url;
    }

    public static JobPostImage createEntity(ImageDto imageDto) {
        return JobPostImage.builder()
                .storeImgName(imageDto.getStoreImgName())
                .s3Url(imageDto.getS3Url())
                .build();
    }
}
