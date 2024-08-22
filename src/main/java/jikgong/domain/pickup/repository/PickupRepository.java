package jikgong.domain.pickup.repository;

import jikgong.domain.pickup.entity.Pickup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupRepository extends JpaRepository<Pickup, Long> {

    @Modifying
    @Query("delete from Pickup p where p.jobPost.id = :jobPostId")
    void deleteByJobPost(@Param("jobPostId") Long jobPostId);
}
