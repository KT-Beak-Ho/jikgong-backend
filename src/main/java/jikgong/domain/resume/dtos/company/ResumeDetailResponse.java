package jikgong.domain.resume.dtos.company;

import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.domain.resume.entity.Resume;
import jikgong.domain.skill.dtos.SkillResponse;
import jikgong.global.utils.AgeTransfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResumeDetailResponse {
    private Long resumeId;
    private Integer career; // 경력
    private List<SkillResponse> skillResponseList; // 경력 상세 정보

    // 선호 시간
    private LocalTime preferTimeStart;
    private LocalTime preferTimeEnd;

    // 요구 혜택
    private Boolean meal; // 식사 제공 여부
    private Boolean pickup; // 픽업 여부
    private Park park; // 주차 가능 여부

    private MemberResponse memberResponse; // 노동자 정보

    public static ResumeDetailResponse from(Resume resume) {
        // skill 리스트
        List<SkillResponse> skillResponseList = resume.getSkillList().stream()
                .map(SkillResponse::from)
                .collect(Collectors.toList());

        return ResumeDetailResponse.builder()
                .resumeId(resume.getId())
                .career(resume.getCareer())
                .skillResponseList(skillResponseList)

                .preferTimeStart(resume.getPreferTimeStart())
                .preferTimeEnd(resume.getPreferTimeEnd())

                .meal(resume.getAvailableInfo().getMeal())
                .pickup(resume.getAvailableInfo().getPickup())
                .park(resume.getAvailableInfo().getPark())

                .memberResponse(MemberResponse.from(resume.getMember()))
                .build();
    }

    @Builder
    @Getter
    public static class MemberResponse {
        private Long memberId;
        private String workerName; // 이름
        private Integer age; // 나이
        private Gender gender; // 성별

        private String address; // 위치
        private Integer workTimes; // 출역 횟수
        private Float participationRate; // 참여율

        public static MemberResponse from(Member member) {
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

            return MemberResponse.builder()
                    .memberId(member.getId())
                    .workerName(member.getWorkerInfo().getWorkerName())
                    .age(AgeTransfer.getAgeByBirth(member.getWorkerInfo().getBrith()))
                    .gender(member.getWorkerInfo().getGender())
                    .address(mainLocation.map(location -> location.getAddress().getAddress()).orElse(null))
                    .workTimes(workTimes)
                    .participationRate(participationRate)
                    .build();
        }
    }
}
