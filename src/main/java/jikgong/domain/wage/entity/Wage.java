package jikgong.domain.wage.entity;


import jakarta.persistence.*;
import jikgong.domain.member.entity.Member;
import lombok.AccessLevel;
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

    private String memo; // 메모
    private Float dailyWage; // 하루 임금
    private LocalDateTime wageAt; // 임금 받은 날자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
