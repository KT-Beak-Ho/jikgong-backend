package jikgong.domain.offerWorkDate.entity;

import lombok.Getter;

@Getter
public enum OfferWorkStatus {
    OFFER_ACCEPTED("제안 수락"),
    OFFER_REJECTED("제안 거절"),
    OFFER_PENDING("제안 대기");

    private final String description;

    OfferWorkStatus(String description) {
        this.description = description;
    }
}
