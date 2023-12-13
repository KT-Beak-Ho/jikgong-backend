package jikgong.domain.certification.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jikgong.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Certification extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "certification_id")
    private Long id;

    private String storeImgName; // 저장된 이미지 명 (uuid)
    private String s3Url; // s3 url

    @Builder
    public Certification(String storeImgName, String s3Url) {
        this.storeImgName = storeImgName;
        this.s3Url = s3Url;
    }


}
