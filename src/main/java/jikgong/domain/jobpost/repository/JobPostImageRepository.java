package jikgong.domain.jobpost.repository;

import jikgong.domain.jobpost.entity.JobPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostImageRepository extends JpaRepository<JobPostImage, Long> {

}
