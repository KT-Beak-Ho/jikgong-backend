package jikgong.global.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.fcm.dtos.FCMNotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class FCMNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;

    public void sendNotificationByToken(FCMNotificationRequestDto requestDto) {

        Member member = memberRepository.findById(requestDto.getTargetMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getDeviceToken() != null) {
            Notification notification = Notification.builder()
                    .setTitle(requestDto.getTitle())
                    .setBody(requestDto.getBody())
                    // .setImage(requestDto.getImage())
                    .build();

            Message message = Message.builder()
                    .setToken(member.getDeviceToken())
                    .setNotification(notification)
                    // .putAllData(requestDto.getData())
                    .build();
            try {
                firebaseMessaging.send(message);
                log.info("FCM 알림 전송 완료. targetMember: " + member.getPhone());
            } catch (FirebaseMessagingException e) {
                log.error("FCM 발송 중 에러");
                throw new CustomException(ErrorCode.FCM_ERROR);
            }
        } else {
            throw new CustomException(ErrorCode.FCM_FIREBASE_TOKEN_NOT_FOUND);
        }
    }
}
