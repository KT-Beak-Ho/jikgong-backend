package jikgong.domain.certification.service;

import jikgong.domain.certification.dtos.CertificationResponse;
import jikgong.domain.certification.entity.Certification;
import jikgong.domain.certification.repository.CertificationRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.handler.ImageDto;
import jikgong.global.handler.S3Handler;
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

    public Long saveCertification(Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ImageDto imageDto = s3Handler.uploadImage(file);
        Certification certification = Certification.builder()
                .storeImgName(imageDto.getStoreImgName())
                .s3Url(imageDto.getS3Url())
                .build();
        Certification savedCertification = certificationRepository.save(certification);

        // 회원 엔티티와 연관 관계 세팅
        member.setCertification(certification);

        return savedCertification.getId();
    }


    public CertificationResponse findCertification(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Certification certification = member.getCertification();

        if (certification == null) {
            throw new CustomException(ErrorCode.CERTIFICATION_NOT_FOUND);
        }

        return CertificationResponse.from(certification);
    }

    public void checkAndDeleteCertification(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Certification certification = member.getCertification();

        if (certification != null) {
            member.setCertification(null);
            s3Handler.deleteImage(List.of(certification.getStoreImgName()));
            log.info("기존에 있던 경력 증명서 제거");
        }
    }
}
