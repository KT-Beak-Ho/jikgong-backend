package jikgong.global.feignclient.client;

import feign.Headers;
import jikgong.global.alimtalk.dto.AlimTalkRequest;
import jikgong.global.alimtalk.dto.AlimTalkResponse;
import jikgong.global.config.CoreFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "alimTalk", url = "https://sens.apigw.ntruss.com", configuration = CoreFeignConfiguration.class)
public interface AlimTalkClient {

    @RequestMapping(method = RequestMethod.POST, value = "/alimtalk/v2/services/{serviceID}/messages")
    @Headers("Content-Type: application/json")
    AlimTalkResponse callAlimTalkApi(
            @PathVariable("serviceID") String serviceID,
//            @RequestHeader("Content-Type") String contentType,
            @RequestHeader("x-ncp-iam-access-key") String key,
            @RequestHeader("x-ncp-apigw-timestamp") String timestamp,
            @RequestHeader("x-ncp-apigw-signature-v2") String signature,
            @RequestBody AlimTalkRequest request
            );
}
