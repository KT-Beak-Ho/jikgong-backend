package jikgong.domain.apply.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import jikgong.domain.workdate.entity.WorkDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Apply extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "apply_id")
    private Long id;

    private LocalDateTime statusDecisionTime; // 신청 처리 시간

    @Enumerated(value = EnumType.STRING)
    private ApplyStatus status; // 신청 현황

    private Boolean isOffer; // Offer로 인해 생성된 Apply인지 여부

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_date_id")
    private WorkDate workDate;

    @Builder
    public Apply(ApplyStatus status, Boolean isOffer, Member member, WorkDate workDate) {
        this.status = status;
        this.isOffer = isOffer;
        this.member = member;
        this.workDate = workDate;
    }


    // 제안 시 offered apply 엔티티 리스트 생성
    public static List<Apply> createEntityList(Member member, List<WorkDate> workDateList) {
        List<Apply> applyList = new ArrayList<>();

        for (WorkDate workDate : workDateList) {
            Apply apply = Apply.builder()
                .status(ApplyStatus.OFFERED)
                .isOffer(true)
                .member(member)
                .workDate(workDate)
                .build();
            applyList.add(apply);
        }
        return applyList;
    }

    public void updateStatus(ApplyStatus status, LocalDateTime now) {
        this.statusDecisionTime = now;
        this.status = status;
    }
}
