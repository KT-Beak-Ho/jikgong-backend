package jikgong.domain.resume.dtos.company;

import jikgong.domain.common.Address;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.jobpost.entity.Park;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.domain.resume.entity.Resume;
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
public class ResumeListResponse {
    private Long resumeId;
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

    // 요구 혜택
    private Boolean meal; // 식사 제공 여부
    private Boolean pickup; // 픽업 여부
    private Park park; // 주차 가능 여부

    public static ResumeListResponse from(Resume resume, Address projectAddress) {
        Member member = resume.getMember();

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
        List<String> skillList = resume.getSkillList().stream()
                .map(skill -> skill.getTech().getDescription())
                .collect(Collectors.toList());

        // 거리 계산
        Double distance = null;
        if (mainLocation.isPresent()) {
            distance = DistanceCal.getDistance(projectAddress, mainLocation.get());
        }

        return ResumeListResponse.builder()
                .resumeId(resume.getId())
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .age(AgeTransfer.getAgeByBirth(member.getWorkerInfo().getBrith()))
                .gender(member.getWorkerInfo().getGender())
                .career(resume.getCareer())
                .address(mainLocation.map(location -> location.getAddress().getAddress()).orElse(null))
                .distance(distance)
                .workTimes(workTimes)
                .participationRate(participationRate)
                .skillList(skillList)
                .meal(resume.getAvailableInfo().getMeal())
                .pickup(resume.getAvailableInfo().getPickup())
                .park(resume.getAvailableInfo().getPark())
                .build();
    }
}
