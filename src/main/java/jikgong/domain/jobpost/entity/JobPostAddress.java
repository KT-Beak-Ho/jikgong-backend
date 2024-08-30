package jikgong.domain.jobpost.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostAddress {

    private String address; // 주소
    private Float latitude; // 위도
    private Float longitude; // 경도

    // 시, 구 필터를 위한 필드
    private String city; // 시
    private String district; // 구
}
