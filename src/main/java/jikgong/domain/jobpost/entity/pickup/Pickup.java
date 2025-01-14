package jikgong.domain.jobpost.entity.pickup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Pickup {

    @Id
    @GeneratedValue
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
