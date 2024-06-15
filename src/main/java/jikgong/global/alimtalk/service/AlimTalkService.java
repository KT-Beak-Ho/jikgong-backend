package jikgong.global.alimtalk.service;

import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jikgong.global.feignclient.client.AlimTalkClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlimTalkService {

    private final AlimTalkClient alimTalkClient;

    @Value("${ncp.service-id}")
    private String serviceID;
    @Value("${ncp.access-key}")
    private String ncpAccessKey;
    @Value("${ncp.secret-key}")
    private String ncpSecretKey;
    @Value("${ncp.plus-friend-id}")
    private String plusFriendId;

    /**
     * 사업자 등록 시 AlimTalk 개발 진행
     */
    public void sendAlimTalk(String to, String templateCode, String content) {

//        String alimTalkSignatureRequestUrl = "/alimtalk/v2/services/" + serviceID + "/messages";
//        try {
//            // signature 생성
//            String[] signatureArray =
//                    makePostSignature(ncpAccessKey, ncpSecretKey, alimTalkSignatureRequestUrl);
//
//            // body 설정
//            AlimTalkRequest alimTalkRequest = AlimTalkRequest.builder()
//                    .plusFriendId(plusFriendId)
//                    .templateCode(templateCode)
//                    .messages(List.of(new MessageRequest(to, content)))
//                    .build();
//
//            AlimTalkResponse alimTalkResponse = alimTalkClient.callAlimTalkApi(
//                    serviceID,
////                    "charset=UTF-8",
//                    ncpAccessKey,
//                    signatureArray[0],
//                    signatureArray[1],
//                    alimTalkRequest);
//
//            log.info("status: " + alimTalkResponse.getStatusCode());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    public String[] makePostSignature(String accessKey, String secretKey, String url) {
        String[] result = new String[2];
        try {
            String timeStamp = String.valueOf(Instant.now().toEpochMilli()); // current timestamp (epoch)
            String space = " "; // space
            String newLine = "\n"; // new line
            String method = "POST"; // method

            String message =
                new StringBuilder()
                    .append(method)
                    .append(space)
                    .append(url)
                    .append(newLine)
                    .append(timeStamp)
                    .append(newLine)
                    .append(accessKey)
                    .toString();

            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            String encodeBase64String = Base64.encodeBase64String(rawHmac);

            result[0] = timeStamp;
            result[1] = encodeBase64String;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
