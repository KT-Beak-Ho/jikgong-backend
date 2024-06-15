package jikgong.domain.scrap.repository;

import java.util.Optional;
import java.util.Set;
import jikgong.domain.scrap.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    @Query("select s from Scrap s where s.member.id = :memberId and s.jobPost.id = :jobPostId")
    Optional<Scrap> findByMemberAndJobPost(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId);

    @Query("select s.jobPost.id from Scrap s where s.member.id = :memberId")
    Set<Long> findScrapJobPostIdByMember(@Param("memberId") Long memberId);
}
