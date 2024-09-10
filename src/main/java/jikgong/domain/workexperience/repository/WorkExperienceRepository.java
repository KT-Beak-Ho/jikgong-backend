package jikgong.domain.workexperience.repository;

import java.util.List;
import jikgong.domain.workexperience.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {

    /**
     * 회원 정보 수정
     */
    @Modifying
    @Query("delete from WorkExperience we where we.member.id = :memberId and we.id not in :idList")
    void deleteWorkExperienceNotInIdList(@Param("memberId") Long memberId, @Param("idList") List<Long> idList);
}
