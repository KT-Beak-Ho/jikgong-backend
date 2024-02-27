package jikgong.domain.offer.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Offer extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "offer_id")
    private Long id;

    private OfferStatus offerStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Member company;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Member worker;

    // 양방향 매핑
    @OneToMany(mappedBy = "offer")
    private List<OfferWorkDate> offerWorkDateList = new ArrayList<>();

    @Builder
    public Offer(Member company, Member worker) {
        this.offerStatus = OfferStatus.OFFER;
        this.company = company;
        this.worker = worker;

        this.offerWorkDateList = new ArrayList<>();
    }

    public static Offer createEntity(Member company, Member worker) {
        return Offer.builder()
                .company(company)
                .worker(worker)
                .build();
    }

    public void cancelOffer() {
        this.offerStatus = OfferStatus.OFFER_CANCEL;
    }
}