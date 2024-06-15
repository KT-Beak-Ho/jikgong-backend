package jikgong.domain.profit.entity;


import jakarta.persistence.*;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.profit.dto.history.ProfitModifyRequest;
import jikgong.domain.profit.dto.history.ProfitSaveRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Profit {
    @Id @GeneratedValue
    @Column(name = "profit_id")
    private Long id;

    private String title;
    private LocalDate date; // 근무 날짜
    private LocalTime startTime; // 근무 시작 시간
    private LocalTime endTime; // 근무 종료 시간
    private Integer wage; // 하루 임금
    private Tech tech; // 직종

    @Enumerated(EnumType.STRING)
    private ProfitType profitType; // 입력 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Profit(String title, LocalDate date, LocalTime startTime, LocalTime endTime, Integer wage, Tech tech, Member member, ProfitType profitType) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.wage = wage;
        this.tech = tech;
        this.profitType = profitType;
        this.member = member;
    }

    public static Profit createEntity(ProfitSaveRequest request, Member member) {
        return Profit.builder()
                .title(request.getTitle())
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .wage(request.getWage())
                .tech(request.getTech())
                .profitType(request.getWageType())
                .member(member)
                .build();
    }

    // 임금 지급 내역 수정
    public void modifyProfit(ProfitModifyRequest request) {
        this.title = request.getTitle();
        this.date = request.getDate();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
        this.wage = request.getWage();
        this.tech = request.getTech();
        this.profitType = request.getProfitType();
    }
}
