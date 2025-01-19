package jikgong.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImgType {

    EDUCATION_CERTIFICATE("교육 이수증"),
    WORKER_CARD("근로자 카드"),
    JOB_POST("모집 공고");

    private final String description;
}
