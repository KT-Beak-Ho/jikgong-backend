package jikgong.domain.resume.dtos.worker;

import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.resume.entity.Resume;
import jikgong.domain.skill.dtos.SkillResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
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

                .build();
    }
}
