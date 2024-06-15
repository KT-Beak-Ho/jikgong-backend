package jikgong.domain.project.repository;

import jikgong.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * find by id and member
     */
    @Query("select p from Project p where p.member.id = :memberId and p.id = :projectId")
    Optional<Project> findByIdAndMember(@Param("memberId") Long memberId, @Param("projectId") Long projectId);

    /**
     * 프로젝트 목록 조회
     */
    @Query("select p from Project p where p.member.id = :memberId and p.endDate < :now")
    List<Project> findCompletedProject(@Param("memberId") Long memberId, @Param("now") LocalDate now);

    @Query("select p from Project p where p.member.id = :memberId and p.startDate < :now and p.endDate > :now")
    List<Project> findInProgressProject(@Param("memberId") Long memberId, @Param("now") LocalDate now);

    @Query("select p from Project p where p.member.id = :memberId and p.startDate > :now")
    List<Project> findPlannedProject(@Param("memberId") Long memberId, @Param("now") LocalDate now);


    /**
     * 기업 검색
     */
    @Query("select p from Project p where p.member.id = :memberId and p.endDate > :now")
    List<Project> findNotCompletedProject(@Param("memberId") Long memberId, @Param("now") LocalDate now);
}
