package jikgong.domain.offer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Offer extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "offer_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private OfferStatus offerStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Member company;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Member worker;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    // 양방향 매핑
    @OneToMany(mappedBy = "offer")
    private List<OfferWorkDate> offerWorkDateList = new ArrayList<>();

    @Builder
    public Offer(Member company, Member worker, JobPost jobPost) {
        this.offerStatus = OfferStatus.OFFER;
        this.company = company;
        this.worker = worker;
        this.jobPost = jobPost;

        this.offerWorkDateList = new ArrayList<>();
    }

    public static Offer createEntity(Member company, Member worker, JobPost jobPost) {
        return Offer.builder()
            .company(company)
            .worker(worker)
            .jobPost(jobPost)
            .build();
    }

    public void cancelOffer() {
        this.offerStatus = OfferStatus.OFFER_CANCEL;
    }
}