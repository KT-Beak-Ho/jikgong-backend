package jikgong.global.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ImageDto {

    private String storeImgName; // 저장된 이미지 명 (uuid)
    private String s3Url; // s3 url
    private boolean isThumbnail; // 썸네일 여부
}
