package jikgong.domain.headHunting.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OfferDate {
    @Id
    @GeneratedValue
    @Column(name = "offer_date_id")
    private Long id;

    private LocalDate offerDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_hunting_id")
    private HeadHunting headHunting;

    @Builder
    public OfferDate(LocalDate offerDate, HeadHunting headHunting) {
        this.offerDate = offerDate;
        this.headHunting = headHunting;
    }

    public static List<OfferDate> createEntityList(HeadHunting headHunting, List<LocalDate> workDateList) {
        List<OfferDate> offerDateList = new ArrayList<>();
        for (LocalDate offerDate : workDateList) {
            offerDateList.add(OfferDate.builder().offerDate(offerDate).headHunting(headHunting).build());
        }
        return offerDateList;
    }
}
