package jikgong.domain.wage.entity;


import jakarta.persistence.*;
import jikgong.domain.member.entity.Member;
import jikgong.domain.wage.dtos.WageModifyRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Wage {
    @Id @GeneratedValue
    @Column(name = "wage_id")
    private Long id;

    private Integer dailyWage; // 하루 임금
    private String memo; // 메모
    private String companyName; // 회사 명
    private LocalDateTime startTime; // 근무 시작 시간
    private LocalDateTime endTime; // 근무 종료 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Wage(Integer dailyWage, String memo, String companyName, LocalDateTime startTime, LocalDateTime endTime, Member member) {
        this.dailyWage = dailyWage;
        this.memo = memo;
        this.companyName = companyName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.member = member;
    }

    // 임금 지급 내역 수정
    public void modifyWage(Integer dailyWage, String memo, String companyName, LocalDateTime startTime, LocalDateTime endTime) {
        this.dailyWage = dailyWage;
        this.memo = memo;
        this.companyName = companyName;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
