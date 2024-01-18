package jikgong.domain.workDate.entity;

import jakarta.persistence.*;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.jobPost.entity.JobPost;
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
public class WorkDate {
    @Id
    @GeneratedValue
    @Column(name = "work_date_id")
    private Long id;

    private LocalDate workDate;
    private Integer recruitNum; // 모집 인원
    private Integer registeredNum; // 모집된 인원

    @ManyToOne
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    // 양방향 매핑
    @OneToMany(mappedBy = "workDate")
    private List<Apply> applyList = new ArrayList<>();

    @Builder
    public WorkDate(LocalDate workDate, Integer recruitNum, JobPost jobPost) {
        this.workDate = workDate;
        this.recruitNum = recruitNum;
        this.registeredNum = 0;
        this.jobPost = jobPost;
    }

    public void plusRegisteredNum(Integer updateCount) {
        this.registeredNum += updateCount;
    }
}
