package jikgong.domain.visaimage.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.visaimage.dto.VisaImageResponse;
import jikgong.domain.visaimage.entity.VisaImage;
import jikgong.domain.visaimage.repository.VisaImageRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VisaImageService {

    private final VisaImageRepository visaImageRepository;
    private final MemberRepository memberRepository;


    /**
     * 로그인한 노동자의 비자 사진 조회
     */
    public VisaImageResponse findMyVisaImage(Long workerId) {
        Member member = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        VisaImage visaImage = member.getVisaImage();

        if (visaImage == null) {
            throw new JikgongException(ErrorCode.VISA_IMAGE_NOT_FOUND);
        }

        return VisaImageResponse.from(visaImage);
    }
}
