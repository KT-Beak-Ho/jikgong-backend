package jikgong.domain.workDate.entity;

import jakarta.persistence.*;
import jikgong.domain.jobPost.entity.JobPost;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WorkDate {
    @Id
    @GeneratedValue
    @Column(name = "work_date_id")
    private Long id;

    private LocalDate workDate;

    @ManyToOne
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    @Builder
    public WorkDate(LocalDate workDate, JobPost jobPost) {
        this.workDate = workDate;
        this.jobPost = jobPost;
    }
}
