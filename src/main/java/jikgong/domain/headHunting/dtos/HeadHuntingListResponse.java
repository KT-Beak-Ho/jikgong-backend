package jikgong.domain.headHunting.dtos;

import jikgong.domain.addressInfo.entity.AddressInfo;
import jikgong.domain.common.Address;
import jikgong.domain.headHunting.entity.HeadHunting;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.global.utils.AgeTransfer;
import jikgong.global.utils.DistanceCal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private Double distance; // 거리

    private Integer workTimes; // 출역 횟수
    private Float participationRate; // 참여율

    // 가능 직종
    private List<String> skillList;

    // 가능 여부
    private Boolean meal; // 식사 제공 여부
    private Boolean pickup; // 픽업 여부
    private Park park; // 주차 가능 여부

    public static HeadHuntingListResponse from(HeadHunting headHunting, Address projectAddress) {
        Member member = headHunting.getMember();
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

        // 거리 계산
        Double distance = null;
        if (mainLocation.isPresent()) {
            distance = DistanceCal.getDistance(projectAddress, mainLocation.get());
        }

        return HeadHuntingListResponse.builder()
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .age(AgeTransfer.getAgeByBirth(member.getWorkerInfo().getBrith()))
                .gender(member.getWorkerInfo().getGender())
                .career(headHunting.getCareer())
                .address(mainLocation.map(location -> location.getAddress().getAddress()).orElse(null))
                .distance(distance)
                .workTimes(workTimes)
                .participationRate(participationRate)
                .skillList(skillList)
                .meal(headHunting.getAvailableInfo().getMeal())
                .pickup(headHunting.getAvailableInfo().getPickup())
                .park(headHunting.getAvailableInfo().getPark())
                .build();
    }
}
