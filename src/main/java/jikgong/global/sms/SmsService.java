package jikgong.global.sms;

import com.google.api.client.util.Base64;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import jikgong.global.feignclient.client.SmsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    @Value("${ncp.access-key}")
    private String accessKey;
    @Value("${ncp.secret-key}")
    private String secretKey;
    @Value("${ncp.service-id}")
    private String serviceId;
    @Value("${ncp.send-number}")
    private String sendNumber;
    private final SmsClient smsClient;

    /**
     * sms 발송
     */
    @Async
    public void sendSms(String phoneNumber, String content)  {
        Long time = System.currentTimeMillis();
        String timestamp = time.toString();
        String signature = null;
        try {
            signature = makeSignature(timestamp);
        } catch (Exception e) {
            throw new JikgongException(ErrorCode.SMS_SEND_FAIL);
        }

        SmsRequest.Message message = new SmsRequest.Message(phoneNumber, null, null);

        // 요청 request 생성
        SmsRequest smsRequest = new SmsRequest(
            "SMS",
            "COMM",
            "82",
            this.sendNumber,
            null,
            content,
            List.of(message),
            null,
            null,
            null
        );

        SmsResponse smsResponse = smsClient.sendSms(
            "application/json; charset=utf-8",
            timestamp,
            this.accessKey,
            signature,
            this.serviceId,
            smsRequest
        );

        // 예외 처리
        exceptionHandler(smsResponse);
    }

    // 서명 생성
    private String makeSignature(String timestamp)
        throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        // URL 인코딩된 serviceId 사용
        String encodedServiceId = URLEncoder.encode(this.serviceId, "UTF-8");
        String url = "/sms/v2/services/" + encodedServiceId + "/messages";

        String message = new StringBuilder()
            .append(method)
            .append(space)
            .append(url)
            .append(newLine)
            .append(timestamp)
            .append(newLine)
            .append(this.accessKey)
            .toString();

        SecretKeySpec signingKey = new SecretKeySpec(this.secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    // 예외 처리
    private void exceptionHandler(SmsResponse smsResponse) {
        switch (smsResponse.getStatusCode()) {
            case "202":
                log.info("문자 인증 발송 완료");
                break;
            case "400":
                throw new JikgongException(ErrorCode.SMS_BAD_REQUEST);
            case "401":
                throw new JikgongException(ErrorCode.SMS_UNAUTHORIZED);
            case "403":
                throw new JikgongException(ErrorCode.SMS_FORBIDDEN);
            case "404":
                throw new JikgongException(ErrorCode.SMS_NOT_FOUND);
            case "429":
                throw new JikgongException(ErrorCode.SMS_TOO_MANY_REQUESTS);
            case "500":
                throw new JikgongException(ErrorCode.SMS_INTERNAL_SERVER_ERROR);
            default:
                throw new JikgongException(ErrorCode.SMS_SEND_FAIL);  // 기타 에러
        }
    }
}
