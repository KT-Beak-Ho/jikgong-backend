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
    @Column(name = "apply_id")
    private Long id;

    private WorkStatus status; // 출근, 퇴근, 결근, 조퇴

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_date_id")
    private WorkDate workDate;

    // todo: jobPost -> workDate 로 변경

    @Builder
    public History(WorkStatus status, Member member, WorkDate workDate) {
        this.status = status;
        this.member = member;
        this.workDate = workDate;
    }

    public static History createEntity(WorkStatus workStatus, Member member, WorkDate workDate) {
        return History.builder()
                .status(workStatus)
                .member(member)
                .workDate(workDate)
                .build();
    }
}
