package jikgong.domain.certification.service;

import jikgong.domain.certification.dto.CertificationResponse;
import jikgong.domain.certification.entity.Certification;
import jikgong.domain.certification.repository.CertificationRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.s3.ImageDto;
import jikgong.global.s3.S3Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CertificationService {
    private final CertificationRepository certificationRepository;
    private final S3Handler s3Handler;
    private final MemberRepository memberRepository;

    /**
     * 경력 인증서 저장
     * s3 업로드 후 저장
     */
    public Long saveCertification(Long workerId, MultipartFile file) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ImageDto imageDto = s3Handler.uploadCertification(file);
        Certification certification = Certification.builder()
                .storeImgName(imageDto.getStoreImgName())
                .s3Url(imageDto.getS3Url())
                .build();
        Certification savedCertification = certificationRepository.save(certification);

        // 회원 엔티티와 연관 관계 세팅
        worker.setCertification(certification);

        return savedCertification.getId();
    }


    /**
     * 경력 인증서 조회
     */
    @Transactional(readOnly = true)
    public CertificationResponse findCertification(Long workerId) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Certification certification = worker.getCertification();

        if (certification == null) {
            throw new CustomException(ErrorCode.CERTIFICATION_NOT_FOUND);
        }

        return CertificationResponse.from(certification);
    }

    /**
     * 경력 증명서 제거
     */
    public void checkAndDeleteCertification(Long workerId) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Certification certification = worker.getCertification();

        if (certification != null) {
            worker.setCertification(null);
            s3Handler.deleteImage(List.of(certification.getStoreImgName()));
            log.info("기존에 있던 경력 증명서 제거");
        }
    }
}
