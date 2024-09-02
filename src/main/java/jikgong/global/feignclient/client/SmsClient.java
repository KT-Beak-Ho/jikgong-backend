package jikgong.global.feignclient.client;

import jikgong.global.sms.SmsRequest;
import jikgong.global.sms.SmsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "smsClient", url = "https://sens.apigw.ntruss.com")
public interface SmsClient {

    @PostMapping(value = "/sms/v2/services/{serviceId}/messages", consumes = "application/json", produces = "application/json")
    SmsResponse sendSms(
        @RequestHeader("Content-Type") String contentType,
        @RequestHeader("x-ncp-apigw-timestamp") String timestamp,
        @RequestHeader("x-ncp-iam-access-key") String accessKey,
        @RequestHeader("x-ncp-apigw-signature-v2") String signature,
        @PathVariable("serviceId") String serviceId,
        @RequestBody SmsRequest smsRequest
    );
}