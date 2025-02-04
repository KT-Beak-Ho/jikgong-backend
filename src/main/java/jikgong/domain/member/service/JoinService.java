package jikgong.domain.member.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jikgong.domain.common.Address;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.dto.join.JoinCompanyRequest;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
import jikgong.domain.member.dto.join.VerificationAccountRequest;
import jikgong.domain.member.dto.join.VerificationAccountResponse;
import jikgong.domain.member.dto.join.VerificationSmsRequest;
import jikgong.domain.member.dto.join.VerificationSmsResponse;
import jikgong.domain.member.entity.Company;
import jikgong.domain.member.entity.ImgType;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.entity.Worker;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.workexperience.entity.WorkExperience;
import jikgong.domain.workexperience.repository.WorkExperienceRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import jikgong.global.s3.ImageDto;
import jikgong.global.s3.S3Handler;
import jikgong.global.sms.SmsService;
import jikgong.global.utils.RandomCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JoinService {

    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final S3Handler s3Handler;
    private final PasswordEncoder encoder;
    private final SmsService smsService;

    /**
     * 노동자 회원가입 위치 정보 저장
     */
    public Long joinWorkerMember(JoinWorkerRequest request,
        MultipartFile educationCertificateImage,
        MultipartFile workerCardImage, MultipartFile signatureImage) {
        // loginId 중복 체크
        validationLoginId(request.getLoginId());
        // 휴대폰 중복 체크
        validationPhone(request.getPhone());

        // 노동자 정보
        Worker worker = Worker.createWorker(request);

        // 교육 이수증, 근로자 카드 이미지
        if (educationCertificateImage != null) {
            ImageDto educationCertificateImagePath = s3Handler.uploadImageWithImgType(
                educationCertificateImage, ImgType.EDUCATION_CERTIFICATE);
            worker.updateEducationCertificateImgPath(educationCertificateImagePath.getS3Url());
        }
        if (workerCardImage != null) {
            ImageDto workerCardImagePath = s3Handler.uploadImageWithImgType(
                workerCardImage, ImgType.WORKER_CARD);
            worker.updateWorkerCardImgPath(
                workerCardImagePath.getS3Url(), request.getWorkerCardNumber()
            );
        }

        // 공통 부분
        Member member = Member.createMember(
            request, encoder.encode(request.getPassword()), worker
        );

        // 서명 이미지
        if (signatureImage != null) {
            ImageDto signatureImagePath = s3Handler.uploadImageWithImgType(
                signatureImage, ImgType.SIGNATURE);
            member.updateSignatureImagePath(signatureImagePath.getS3Url());
        }

        // 위치 정보 생성
        Location location = Location.createEntity(request, member, true);

        // 경력 정보 생성
        List<WorkExperience> workExperienceList =
            WorkExperience.from(request.getWorkExperienceRequest(), member);

        Member savedMember = memberRepository.save(member); // 회원 저장
        workExperienceRepository.saveAll(workExperienceList); // 경력 정보 저장
        locationRepository.save(location); // 위치 정보 저장

        log.info("노동자 회원 가입 완료");
        return savedMember.getId();
    }

    /**
     * 기업 회원가입
     */
    public Long joinCompanyMember(JoinCompanyRequest request, MultipartFile signatureImage) {
        // loginId 중복 체크
        validationLoginId(request.getLoginId());
        // 휴대폰 중복 체크
        validationPhone(request.getPhone());

        // 기업 정보
        Company company = Company.createCompany(request);

        // 공통 부분
        Member member = Member.createMember(
            request, encoder.encode(request.getPassword()), company
        );

        // 서명 이미지
        if (signatureImage != null) {
            ImageDto signatureImagePath = s3Handler.uploadImageWithImgType(
                signatureImage, ImgType.SIGNATURE);
            member.updateSignatureImagePath(signatureImagePath.getS3Url());
        }

        log.info("기업 회원 가입 완료");
        return memberRepository.save(member).getId(); // 회원 저장
    }

    /**
     * 휴대폰 중복 체크
     */
    public void validationPhone(String phone) {
        Optional<Member> member = memberRepository.findByPhone(phone);
        if (member.isPresent()) {
            throw new JikgongException(ErrorCode.MEMBER_PHONE_EXIST);
        }
    }

    /**
     * loginId 중복 체크
     */
    public void validationLoginId(String login) {
        Optional<Member> member = memberRepository.findByLoginId(login);
        if (member.isPresent()) {
            throw new JikgongException(ErrorCode.MEMBER_LOGIN_ID_EXIST);
        }
    }

    /**
     * 휴대폰 인증
     */
    public VerificationSmsResponse sendVerificationSms(VerificationSmsRequest request) {
        // 6자리 랜덤 코드 생성
        String authCode = RandomCode.createAuthCode();
        String content = "[직공]\n본인확인 인증번호: [" + authCode + "]";
        smsService.sendSms(request.getPhone(), content);

        return new VerificationSmsResponse(authCode);
    }

    /**
     * 계좌 인증
     */
    public VerificationAccountResponse verificationAccount(VerificationAccountRequest request) {

        // todo: 계좌 인증

        return new VerificationAccountResponse("52");
    }
}
