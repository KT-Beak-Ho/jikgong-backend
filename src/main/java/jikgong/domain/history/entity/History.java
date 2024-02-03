package jikgong.domain.history.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import jikgong.domain.workDate.entity.WorkDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class History extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "history_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private WorkStatus startStatus; // 출근, 퇴근

    @Enumerated(EnumType.STRING)
    private WorkStatus endStatus; // 결근, 조퇴

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_date_id")
    private WorkDate workDate;


    @Builder
    public History(WorkStatus startStatus, WorkStatus endStatus, Member member, WorkDate workDate) {
        this.startStatus = startStatus;
        this.endStatus = endStatus;
        this.member = member;
        this.workDate = workDate;
    }

    public static History createEntity(WorkStatus workStatus, Member member, WorkDate workDate) {
        return History.builder()
                .startStatus(workStatus)
                .member(member)
                .workDate(workDate)
                .build();
    }
}
