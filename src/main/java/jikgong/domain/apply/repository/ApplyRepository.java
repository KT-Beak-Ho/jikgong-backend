package jikgong.domain.apply.repository;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {
    @Query("select a from Apply a where a.member.id = :memberId and a.status = :status")
    List<Apply> findByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") ApplyStatus status);

    @Query("select a from Apply a join fetch Member m where a.jobPost.member.id = :memberId and a.jobPost.id = :jobPostId and a.status = :status")
    List<Apply> findByJobPostIdAndMemberIdAndStatus(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("status") ApplyStatus status);

    @Query("select a from Apply a where a.jobPost.member.id = :memberId and a.jobPost.id = :jobPostId")
    Optional<Apply> findByMemberIdAndJobPostId(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId);

    @Query("select a from Apply a where a.jobPost.member.id = :memberId and a.jobPost.id = :jobPostId and a.member.id = :targetMemberId")
    Optional<Apply> checkAppliedAndAuthor(@Param("memberId") Long memberId, @Param("targetMemberId") Long targetMemberId, @Param("jobPostId") Long jobPostId);
}
