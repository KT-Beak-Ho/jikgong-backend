package jikgong.global.sms;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SmsResponse {

    private String requestId;
    private String requestTime; // DateTime
    private String statusCode; // 202 or 4xx
    private String statusName; // success or fail
}
