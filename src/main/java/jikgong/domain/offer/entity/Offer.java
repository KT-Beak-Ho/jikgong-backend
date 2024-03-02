package jikgong.domain.offer.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.jobPost.entity.JobPost;
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