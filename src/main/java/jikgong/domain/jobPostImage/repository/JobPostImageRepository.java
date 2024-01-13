package jikgong.domain.jobPostImage.repository;

import jikgong.domain.jobPostImage.entity.JobPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostImageRepository extends JpaRepository<JobPostImage, Long> {
    @Query("select j from JobPostImage j where j.jobPost.member.id = :memberId and j.jobPost.id = :jobPostId")
    List<JobPostImage> findByMemberAndJobPost(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId);
}
