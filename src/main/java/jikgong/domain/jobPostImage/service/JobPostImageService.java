package jikgong.domain.jobPostImage.service;

import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.jobPostImage.entity.JobPostImage;
import jikgong.domain.jobPostImage.repository.JobPostImageRepository;
import jikgong.global.handler.S3Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobPostImageService {
    private final JobPostImageRepository jobPostImageRepository;
    private final S3Handler s3Handler;

    public void deleteEntityAndS3(Long memberId, Long jobPostId) {
        List<JobPostImage> jobPostImageList = jobPostImageRepository.findByMemberAndJobPost(memberId, jobPostId);

        // s3 이미지 삭제
        List<String> storedImgList = jobPostImageList.stream()
                .map(JobPostImage::getStoreImgName)
                .collect(Collectors.toList());
        s3Handler.deleteImage(storedImgList);

        jobPostImageRepository.deleteAll(jobPostImageList);
        log.info("image 엔티티 " + jobPostImageList.size() + "개 삭제 완료");
    }
}
