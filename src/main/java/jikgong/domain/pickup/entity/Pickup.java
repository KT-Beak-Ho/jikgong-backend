package jikgong.domain.pickup.entity;

import jakarta.persistence.*;
import jikgong.domain.jobPost.entity.JobPost;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Pickup {

    @Id @GeneratedValue
    @Column(name = "pickup_id")
    private Long id;

    private String address; // 픽업 도로명 주소

    @ManyToOne
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    @Builder
    public Pickup(String address, JobPost jobPost) {
        this.address = address;
        this.jobPost = jobPost;
    }
}
