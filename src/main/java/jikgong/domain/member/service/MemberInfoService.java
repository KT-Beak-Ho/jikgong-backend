package jikgong.domain.member.service;

import jikgong.domain.member.dto.info.PasswordRequest;
import jikgong.domain.member.dto.info.WorkerInfoRequest;
import jikgong.domain.member.dto.info.WorkerInfoResponse;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberInfoService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    /**
     * 노동자: 회원 정보 조회
     */
    @Transactional(readOnly = true)
    public WorkerInfoResponse findWorkerInfo(Long workerId) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        return WorkerInfoResponse.from(worker);
    }

    /**
     * 노동자: 회원 정보 수정
     */
    public void updateWorkerInfo(Long workerId, WorkerInfoRequest request) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        worker.updateWorkerInfo(request);
    }

    /**
     * 비밀번호 확인 후 변경
     */
    public void validationPassword(Long memberId, PasswordRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        if (!encoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new JikgongException(ErrorCode.MEMBER_INVALID_PASSWORD);
        }

        member.updatePassword(encoder.encode(request.getNewPassword()));
    }
}
