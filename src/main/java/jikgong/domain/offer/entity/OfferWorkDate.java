package jikgong.domain.offer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;
import jikgong.domain.workdate.entity.WorkDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OfferWorkDate {

    @Id
    @GeneratedValue
    @Column(name = "offer_work_date_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private OfferWorkDateStatus offerWorkDateStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_date_id")
    private WorkDate workDate;

    @Builder
    public OfferWorkDate(OfferWorkDateStatus offerWorkDateStatus, Offer offer, WorkDate workDate) {
        this.offerWorkDateStatus = offerWorkDateStatus;
        this.offer = offer;
        this.workDate = workDate;
    }

    public static List<OfferWorkDate> createEntityList(Offer offer, List<WorkDate> workDateList) {
        List<OfferWorkDate> offerWorkDateList = new ArrayList<>();
        for (WorkDate workDate : workDateList) {
            OfferWorkDate offerWorkDate = OfferWorkDate.builder()
                .offerWorkDateStatus(OfferWorkDateStatus.OFFER_PENDING)
                .offer(offer)
                .workDate(workDate)
                .build();
            offerWorkDateList.add(offerWorkDate);
        }
        return offerWorkDateList;
    }

    public void processOffer(Boolean isAccept) {
        this.offerWorkDateStatus = isAccept ? OfferWorkDateStatus.OFFER_ACCEPTED : OfferWorkDateStatus.OFFER_REJECTED;
    }
}