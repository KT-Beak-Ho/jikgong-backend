package jikgong.domain.offerworkdate.entity;

import lombok.Getter;

@Getter
public enum OfferWorkDateStatus {
    OFFER_ACCEPTED("제안 수락"),
    OFFER_REJECTED("제안 거절"),
    OFFER_PENDING("제안 대기"),
    OFFER_CANCELED("제안 취소"), // 기업이 취소한 경우
    OFFER_AUTO_CANCELED("자동 취소"); // 출역일이 지난 경우

    private final String description;

    OfferWorkDateStatus(String description) {
        this.description = description;
    }
}
