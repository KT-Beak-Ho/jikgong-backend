package jikgong.global.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ImageDto {
    private String storeImgName; // 저장된 이미지 명 (uuid)
    private String s3Url; // s3 url
}
