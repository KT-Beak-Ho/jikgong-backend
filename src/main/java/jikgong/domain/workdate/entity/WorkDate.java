package jikgong.domain.workdate.entity;

import jakarta.persistence.*;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.global.exception.JikgongException;
import jikgong.global.exception.ErrorCode;
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

    private LocalDate date;
    private Integer recruitNum; // 모집 인원
    private Integer registeredNum; // 모집된 인원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    // 양방향 매핑
    @OneToMany(mappedBy = "workDate")
    private List<Apply> applyList = new ArrayList<>();

    @Builder
    public WorkDate(LocalDate date, Integer recruitNum, JobPost jobPost) {
        this.date = date;
        this.recruitNum = recruitNum;
        this.registeredNum = 0;
        this.jobPost = jobPost;

        this.applyList = new ArrayList<>();
    }

    public void plusRegisteredNum(Integer updateCount) {
        if (this.registeredNum + updateCount > this.recruitNum) {
            throw new JikgongException(ErrorCode.APPLY_OVER_RECRUIT_NUM);
        }
        this.registeredNum += updateCount;
    }

    public void minusRegisteredNum() {
        this.registeredNum -= 1;
    }
}
