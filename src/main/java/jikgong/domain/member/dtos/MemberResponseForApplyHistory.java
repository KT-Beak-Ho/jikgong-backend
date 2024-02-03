package jikgong.domain.member.dtos;

import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.global.utils.AgeTransfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class MemberResponseForApplyHistory {
    /**
     * 요청된 기록을 볼 때 노동자 정보를 담기 위한 클래스
     */
    private Long memberId;
    private String workerName; // 노동자 이름
    private String phone; // 휴대폰 번호
    private Integer age; // 나이
    private String nationality; // 국적
    private Integer workTimes; // 출역 횟수
    private Float participationRate; // 참여율

    public static MemberResponseForApplyHistory from(Member member) {
        // 나이 계산
        int age = AgeTransfer.getAgeByRrn(member.getWorkerInfo().getBrith());

        // 출역 횟수, 참여율
        List<History> workHistory = member.getHistoryList().stream()
                .filter(history -> history.getEndStatus() == WorkStatus.FINISH_WORK)
                .collect(Collectors.toList());
        int workTimes = workHistory.size();
        float participationRate = (float) workTimes / (float) member.getHistoryList().size();

        // 출역 내역이 없을 경우 -1
        if (workTimes == 0) {
            participationRate = -1;
        }

        return MemberResponseForApplyHistory.builder()
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .phone(member.getPhone())
                .age(age)
                .nationality(member.getWorkerInfo().getNationality())
                .workTimes(workTimes)
                .participationRate(participationRate)
                .build();
    }
}
