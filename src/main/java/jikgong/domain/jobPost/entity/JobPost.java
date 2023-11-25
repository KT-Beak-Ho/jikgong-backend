package jikgong.domain.jobPost.entity;

import jakarta.persistence.*;
import jikgong.domain.common.Address;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class JobPost {
    @Id @GeneratedValue
    @Column(name = "job_post_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Tech tech;
    private LocalDateTime startTime; // 시작 일시
    private Integer recruitNum; // 모집 인원
    private Integer registeredNum; // 모집 인원
    private Integer wage; // 임금
    private String workDetail; // 작업 상세
    private String preparation; // 준비 사항
    private LocalDateTime expirationTime; // 모집 마감

    @Embedded
    private AvailableInfo availableInfo; // 가능 여부 정보
    @Embedded
    private Address address;

    @Builder
    public JobPost(Tech tech, LocalDateTime startTime, Integer recruitNum, Integer registeredNum, Integer wage, String workDetail, String preparation, AvailableInfo availableInfo, Address address) {
        this.tech = tech;
        this.startTime = startTime;
        this.recruitNum = recruitNum;
        this.registeredNum = registeredNum;
        this.wage = wage;
        this.workDetail = workDetail;
        this.preparation = preparation;
        this.availableInfo = availableInfo;
        this.address = address;
    }
}
