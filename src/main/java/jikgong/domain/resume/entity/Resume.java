package jikgong.domain.resume.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.jobPost.entity.AvailableInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Resume extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "resume_id")
    private Long id;
    private String position; // 직책
    private Integer experienceYear; // 경력
    private LocalDate workStartDate; // 근무 가능 시작 일짜
    private LocalDate workEndDate; // 근무 가능 종료 일짜
    private String preferTime; // 선호 시간

    @Embedded
    private AvailableInfo availableInfo; // 가능 여부 정보

}
