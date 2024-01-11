package jikgong.domain.history.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.history.dtos.HistorySaveRequest;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class History extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "apply_id")
    private Long id;

    private Boolean isWork; // 출근 여부
    private LocalDate workDate; // 출근 일짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    @Builder
    public History(Boolean isWork, LocalDate workDate, Member member, JobPost jobPost) {
        this.isWork = isWork;
        this.workDate = workDate;
        this.member = member;
        this.jobPost = jobPost;
    }

    public static History createEntity(HistorySaveRequest request, Member member, JobPost jobPost) {
        return History.builder()
                .isWork(request.getIsWork())
                .workDate(request.getWorkDate())
                .member(member)
                .jobPost(jobPost)
                .build();
    }
}
