package jikgong.domain.addressInfo.entity;

import jakarta.persistence.*;
import jikgong.domain.jobPost.entity.JobPost;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AddressInfo {

    @Id @GeneratedValue
    @Column(name = "pickup_location_id")
    private Long id;

    private String address; // 픽업 도로명 주소
    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @ManyToOne
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    @Builder
    public AddressInfo(String address, AddressType addressType, JobPost jobPost) {
        this.address = address;
        this.addressType = addressType;
        this.jobPost = jobPost;
    }
}
