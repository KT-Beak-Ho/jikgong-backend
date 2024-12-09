package jikgong.domain.scrap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Scrap extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Scrap(JobPost jobPost, Member member) {
        this.jobPost = jobPost;
        this.member = member;
    }

    public static Scrap createEntity(Member member, JobPost jobPost) {
        return Scrap.builder()
            .member(member)
            .jobPost(jobPost)
            .build();
    }
}
