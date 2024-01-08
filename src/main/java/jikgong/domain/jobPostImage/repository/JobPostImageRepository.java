package jikgong.domain.jobPostImage.repository;

import jikgong.domain.jobPostImage.entity.JobPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostImageRepository extends JpaRepository<JobPostImage, Long> {
}
