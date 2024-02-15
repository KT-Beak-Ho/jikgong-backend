package jikgong.domain.headHunting.dtos.company;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.headHunting.entity.HeadHunting;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.global.utils.AgeTransfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class WorkerInfoResponse {
    private Long memberId;
    private String workerName; // 이름
    private Integer age; // 나이
    private Gender gender; // 성별
    private Integer career; // 경력

    private String address; // 위치
    private Integer workTimes; // 출역 횟수
    private Float participationRate; // 참여율
    private List<String> skillList; // 가능 직종

    private LocalTime preferTimeStart;
    private LocalTime preferTimeEnd;

    // 요구 혜택
    private Boolean meal; // 식사 제공 여부
    private Boolean pickup; // 픽업 여부
    private Park park; // 주차 가능 여부

    private List<LocalDate> cantWorkDateList;

    public static WorkerInfoResponse from(HeadHunting headHunting, List<Apply> findCantWorkDate) {
        Member member = headHunting.getMember();

        // 대표 위치
        Optional<Location> mainLocation = member.getLocationList().stream()
                .filter(Location::getIsMain)
                .findFirst();

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

        // skill 리스트
        List<String> skillList = headHunting.getSkillList().stream()
                .map(skill -> skill.getTech().getDescription())
                .collect(Collectors.toList());

        // 이미 일하는 날짜
        List<LocalDate> cantWorkDateList = findCantWorkDate.stream()
                .map(apply -> apply.getWorkDate().getWorkDate())
                .collect(Collectors.toList());

        return WorkerInfoResponse.builder()
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .age(AgeTransfer.getAgeByBirth(member.getWorkerInfo().getBrith()))
                .gender(member.getWorkerInfo().getGender())
                .career(headHunting.getCareer())

                .address(mainLocation.map(location -> location.getAddress().getAddress()).orElse(null))
                .workTimes(workTimes)
                .participationRate(participationRate)
                .skillList(skillList)

                .preferTimeStart(headHunting.getPreferTimeStart())
                .preferTimeEnd(headHunting.getPreferTimeEnd())

                .meal(headHunting.getAvailableInfo().getMeal())
                .pickup(headHunting.getAvailableInfo().getPickup())
                .park(headHunting.getAvailableInfo().getPark())

                .cantWorkDateList(cantWorkDateList)
                .build();
    }
}
