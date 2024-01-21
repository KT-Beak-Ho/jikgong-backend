package jikgong.domain.wage.entity;


import jakarta.persistence.*;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.wage.dtos.WageModifyRequest;
import jikgong.domain.wage.dtos.WageSaveRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Wage {
    @Id @GeneratedValue
    @Column(name = "wage_id")
    private Long id;

    private String title;
    private LocalDate workDate; // 근무 날짜
    private LocalTime startTime; // 근무 시작 시간
    private LocalTime endTime; // 근무 종료 시간
    private Integer dailyWage; // 하루 임금
    private Tech tech; // 직종

    @Enumerated(EnumType.STRING)
    private WageType wageType; // 입력 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Wage(String title, LocalDate workDate, LocalTime startTime, LocalTime endTime, Integer dailyWage, Tech tech, Member member, WageType wageType) {
        this.title = title;
        this.workDate = workDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dailyWage = dailyWage;
        this.tech = tech;
        this.wageType = wageType;
        this.member = member;
    }

    public static Wage createEntity(WageSaveRequest request, Member member) {
        return Wage.builder()
                .title(request.getTitle())
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .dailyWage(request.getDailyWage())
                .tech(request.getTech())
                .wageType(request.getWageType())
                .member(member)
                .build();
    }

    // 임금 지급 내역 수정
    public void modifyWage(WageModifyRequest request) {
        this.title = request.getTitle();
        this.workDate = request.getWorkDate();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
        this.dailyWage = request.getDailyWage();
        this.tech = request.getTech();
        this.wageType = request.getWageType();
    }
}
