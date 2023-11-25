package jikgong.domain.jobPost.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class AvailableInfo {
    private Boolean meal; // 식사 제공 여부
    private Boolean pickup; // 픽업 여부
    private Boolean safeEquipment; // 안전 장비 제공 여부
    private Boolean park; // 주차 가능 여부
    // todo: 추가 예정
}
