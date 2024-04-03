package jikgong.domain.resume.repository;

import jikgong.domain.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>, ResumeRepositoryCustom {
    /**
     * 일자리 제안
     */
    @Query("select r from Resume r join fetch r.member m where m.id = :memberId and r.id = :resumeId")
    Optional<Resume> findByIdWithMember(@Param("memberId") Long memberId, @Param("resumeId") Long resumeId);
}
