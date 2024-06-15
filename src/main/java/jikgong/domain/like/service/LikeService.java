package jikgong.domain.like.service;

import jikgong.domain.like.entity.Like;
import jikgong.domain.like.repository.LikeRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.entity.Role;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.JikgongException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;

    /**
     * 좋아요 등록
     */
    public Long saveLike(Long senderId, Long receiverId) {
        Optional<Like> existLike = likeRepository.findBySenderIdAndReceiverId(senderId, receiverId);

        // 이미 좋아요 누른 경우
        if (existLike.isPresent()) {
            throw new JikgongException(ErrorCode.LIKE_ALREADY_EXIST);
        }

        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 기업 -> 노동자 요청이 아닌 경우
        if (sender.getRole() != Role.ROLE_COMPANY || receiver.getRole() != Role.ROLE_WORKER) {
            throw new JikgongException(ErrorCode.LIKE_REQUEST_INVALID);
        }

        return likeRepository.save(Like.createEntity(sender, receiver)).getId();
    }

    /**
     * 좋아요 취소
     */
    public void deleteLike(Long senderId, Long receiverId) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 기업 -> 노동자 요청이 아닌 경우
        if (sender.getRole() != Role.ROLE_COMPANY || receiver.getRole() != Role.ROLE_WORKER) {
            throw new JikgongException(ErrorCode.LIKE_REQUEST_INVALID);
        }

        Like like = likeRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new JikgongException(ErrorCode.LIKE_NOT_FOUND));
        likeRepository.delete(like);
    }
}
