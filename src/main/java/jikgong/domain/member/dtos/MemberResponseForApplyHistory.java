package jikgong.domain.member.dtos;

import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.global.utils.AgeTransfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Builder
public class MemberResponseForApplyHistory {
    /**
     * 요청된 기록을 볼 때 노동자 정보를 담기 위한 클래스
     */
    private Long memberId;
    private String workerName; // 노동자 이름
    private Integer age; // 나이
    private String address; // 주소
    private String nationality; // 국적

    public static MemberResponseForApplyHistory from(Member member) {
        // 나이 계산
        int age = AgeTransfer.getAgeByRrn(member.getWorkerInfo().getBrith());

        // 메인 주소 추출
        Optional<Location> mainLocation = member.getLocationList().stream()
                .filter(Location::getIsMain)
                .findFirst();
        String address = mainLocation.isPresent() ? mainLocation.get().getAddress().getAddress() : "주소 정보 없음";

        return MemberResponseForApplyHistory.builder()
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .age(age)
                .address(address)
                .nationality(member.getWorkerInfo().getNationality())
                .build();
    }
}
