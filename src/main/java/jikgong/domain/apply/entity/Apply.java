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

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Apply extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "apply_id")
    private Long id;

    private LocalDateTime statusDecisionTime; // 신청 처리 시간

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

    public void updateStatus(ApplyStatus status, LocalDateTime now) {
        this.statusDecisionTime = now;
        this.status = status;
    }
}
