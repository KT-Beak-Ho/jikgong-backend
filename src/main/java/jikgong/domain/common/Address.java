package jikgong.domain.common;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String address; // 주소
    private Float latitude; // 위도
    private Float longitude; // 경도
}
