package jikgong.domain.resume.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jikgong.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Resume extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "resume_id")
    private Long id;
    private String position; // 직책
    private Integer experienceYear; // 경력

    @Builder
    public Resume(String position, Integer experienceYear) {
        this.position = position;
        this.experienceYear = experienceYear;
    }
}
