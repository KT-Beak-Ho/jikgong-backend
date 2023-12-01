package jikgong.domain.member.dtos;

import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Nationality;
import jikgong.domain.member.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class JoinRequest {
    private String phone;
    private String authCode; // 인증 코드
    private Role role; // 회원 타입
    private String account; // 게좌번호
    private String bank; // 은행

    // 위치 정보
    private String address; // 도로명 주소
    private Float latitude; // 위도
    private Float longitude; // 경도

    // 회사 정보
    private String businessNumber; // 사업자 번호
    private String region; // 지역
    private String companyName; // 회사 명
    private String email; // 이메일
    private String manager; // 담당자 이름
    private String requestContent; // 문의 내용

    // 노동자 정보
    private String workerName; // 노동자 이름
    private String rrnPrefix; // 생년월일
    private Gender gender; // 성별
    private Nationality nationality; // 국적
}
