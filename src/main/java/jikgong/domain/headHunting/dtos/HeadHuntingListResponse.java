package jikgong.domain.headHunting.dtos;

import jikgong.domain.member.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class HeadHuntingListResponse {
    private Long memberId;
    private String workerName; // 이름
    private Integer age; // 나이
    private Gender gender; // 성별
    private Integer career; // 경력
    private String address; // 위치
    private Integer workTimes; // 출역 횟수
    private Float participationRate; // 참여율
}
