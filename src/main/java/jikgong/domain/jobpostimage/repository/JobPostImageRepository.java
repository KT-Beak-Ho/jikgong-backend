package jikgong.domain.jobpostimage.repository;

import jikgong.domain.jobpostimage.entity.JobPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostImageRepository extends JpaRepository<JobPostImage, Long> {
}
