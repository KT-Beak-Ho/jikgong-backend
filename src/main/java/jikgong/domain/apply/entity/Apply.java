package jikgong.domain.apply.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.member.entity.Member;
import jikgong.domain.workDate.entity.WorkDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Apply extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "apply_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private ApplyStatus status; // 신청 현황

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_date_id")
    private WorkDate workDate;

    @Builder
    public Apply(Member member, WorkDate workDate) {
        this.status = ApplyStatus.PENDING;
        this.member = member;
        this.workDate = workDate;
    }
}
