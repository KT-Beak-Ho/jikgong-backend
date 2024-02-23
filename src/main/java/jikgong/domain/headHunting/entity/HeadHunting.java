package jikgong.domain.headHunting.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HeadHunting extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "head_hunting_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Member company;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Member worker;

    @OneToMany(mappedBy = "headHunting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfferDate> offerDateList = new ArrayList<>();

    @Builder
    public HeadHunting(Member company, Member worker) {
        this.company = company;
        this.worker = worker;

        this.offerDateList = new ArrayList<>();
    }

    public static HeadHunting createEntity(Member company, Member worker) {
        return HeadHunting.builder()
                .company(company)
                .worker(worker)
                .build();
    }

    public void addOfferDate(List<OfferDate> offerDateList) {
        this.offerDateList.addAll(offerDateList);
    }
}