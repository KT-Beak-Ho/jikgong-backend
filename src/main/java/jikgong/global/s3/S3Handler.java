package jikgong.global.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jikgong.domain.member.entity.ImgType;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Handler {

    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.resize-bucket}")
    private String resize_bucket;


    public ImageDto uploadImageWithImgType(MultipartFile file, ImgType imgType) {
        String extension; //확장자명
        String contentType = file.getContentType();

        if (ObjectUtils.isEmpty(contentType)) {
            throw new JikgongException(ErrorCode.FILE_NOT_FOUND_EXTENSION);
        } else { //확장자명이 jpeg, png 인 파일들만 받아서 처리
            if (contentType.contains("image/jpeg")) {
                extension = ".jpg";
            } else if (contentType.contains("image/png")) {
                extension = ".png";
            } else {
                log.error("사진이 아닌 파일 입니다.");
                throw new JikgongException(ErrorCode.FILE_NOT_SUPPORTED);
            }
        }

        // 폴더 경로 결정
        String storeImageName = getFolderPathByImgType(imgType) + "/" + createStoreImageName(extension);

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new JikgongException(ErrorCode.FILE_STREAM_FAIL);
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        PutObjectRequest request = new PutObjectRequest(bucket, storeImageName, inputStream, metadata);

        // s3 put
        amazonS3Client.putObject(request);
        log.info("s3에 사진 저장");

        // s3 get
        String s3Url = getImgPath(storeImageName);

        return ImageDto.builder()
            .s3Url(s3Url)
            .storeImgName(storeImageName)
            .build();
    }

    public List<ImageDto> uploadImagesWithImgType(List<MultipartFile> files, ImgType imgType) {
        List<ImageDto> imageDtoList = new ArrayList<>();
        if (files == null) {
            return imageDtoList;
        }

        // 첫번째 이미지는 썸네일로 등록
        boolean isFirst = true; // 첫 번째 이미지를 식별하기 위한 플래그

        for (MultipartFile file : files) {
            String extension; //확장자명
            String contentType = file.getContentType();

            if (ObjectUtils.isEmpty(contentType)) {
                throw new JikgongException(ErrorCode.FILE_NOT_FOUND_EXTENSION);
            } else { //확장자명이 jpeg, png 인 파일들만 받아서 처리
                if (contentType.contains("image/jpeg")) {
                    extension = ".jpg";
                } else if (contentType.contains("image/png")) {
                    extension = ".png";
                } else {
                    log.error("사진이 아닌 파일 입니다.");
                    throw new JikgongException(ErrorCode.FILE_NOT_SUPPORTED);
                }
            }

            // 폴더 경로 결정
            String storeImageName = getFolderPathByImgType(imgType) + "/" + createStoreImageName(extension);

            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream();
            } catch (IOException e) {
                throw new JikgongException(ErrorCode.FILE_STREAM_FAIL);
            }
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest request = new PutObjectRequest(bucket, storeImageName, inputStream, metadata);

            // s3 put
            amazonS3Client.putObject(request);
            log.info("s3에 사진 저장");

            // s3 get
            String s3Url = getImgPath(storeImageName);

            ImageDto imageDto = ImageDto.builder()
                .s3Url(s3Url)
                .storeImgName(storeImageName)
                .isThumbnail(isFirst) // 첫 번째 이미지만 썸네일로 설정
                .build();
            imageDtoList.add(imageDto);

            isFirst = false; // 첫 번째 이미지 처리 후, 플래그 변경
        }
        return imageDtoList;
    }

    public void deleteImage(List<String> storeImgList) {
        for (String storeImgName : storeImgList) {
            if (amazonS3Client.doesObjectExist(bucket, storeImgName)) {
                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, storeImgName);
                amazonS3Client.deleteObject(deleteObjectRequest);
            } else {
                throw new JikgongException(ErrorCode.S3_NOT_FOUND_FILE_NAME);
            }
        }
        log.info("S3 파일 삭제 작업 완료");
    }

    // 저장할 이름 생성
    private String createStoreImageName(String extension) {
        String uuid = UUID.randomUUID().toString();
        return uuid + extension;
    }

    // 버킷에서 이미지 조회
    public String getImgPath(String fileName) {
        if (amazonS3Client.doesObjectExist(bucket, fileName)) {
            return amazonS3Client.getUrl(bucket, fileName).toString();
        } else {
            throw new JikgongException(ErrorCode.S3_NOT_FOUND_FILE_NAME);
        }
    }

    // 이미지 타입에 따라 S3 폴더 경로 결정
    private String getFolderPathByImgType(ImgType imgType) {
        return switch (imgType) {
            case EDUCATION_CERTIFICATE -> "education-certificates";
            case WORKER_CARD -> "worker-cards";
            case JOB_POST -> "job-posts";
            default -> "";
        };
    }

    // resize버킷에서 이미지 조회
    public String getThumbnailImgPath(String fileName) {
        if (amazonS3Client.doesObjectExist(resize_bucket, fileName)) {
            return amazonS3Client.getUrl(resize_bucket, fileName).toString();
        } else {
            // resize_bucket에 파일이 없을 경우, 메인 bucket에서 검색
            if (amazonS3Client.doesObjectExist(bucket, fileName)) {
                log.warn("resize_bucket 에서 조회를 시도했지만, bucket에만 존재");
                return amazonS3Client.getUrl(bucket, fileName).toString();
            } else {
                // 두 버킷 모두에 파일이 없을 경우 예외 발생
                throw new JikgongException(ErrorCode.S3_NOT_FOUND_FILE_NAME);
            }
        }
    }
}
