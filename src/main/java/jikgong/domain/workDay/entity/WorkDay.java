package jikgong.domain.workDay.entity;

import jakarta.persistence.*;
import jikgong.domain.jobPost.entity.JobPost;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WorkDay {
    @Id
    @GeneratedValue
    @Column(name = "work_day_id")
    private Long id;

    private LocalDate workDay;

    @ManyToOne
    @JoinColumn(name = "job_posting_id")
    private JobPost jobPost;

    @Builder
    public WorkDay(LocalDate workDay, JobPost jobPost) {
        this.workDay = workDay;
        this.jobPost = jobPost;
    }
}
