package jikgong.domain.member.dto.info;

import lombok.Data;

@Data
public class StayExpirationResponse {

    private Result result;
    private Data data;

    @lombok.Data
    public static class Result {

        private String code;
        private String extraMessage;
        private String message;
        private String transactionId;
    }

    @lombok.Data
    public static class Data {

        private String resAuthenticity;
        private String resAuthenticityDesc;
        private String resPassportNo;
        private String resNationality;
        private String commBirthDate;
        private String resStatus;
        private String resExpirationDate;
    }
}