package jikgong.domain.headHunting.repository;

import jikgong.domain.headHunting.entity.HeadHunting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeadHuntingRepository extends JpaRepository<HeadHunting, Long>, HeadHuntingRepositoryCustom {
    @Query("select h from HeadHunting h join fetch h.member m where m.id = :memberId")
    Optional<HeadHunting> findWorkerInfoByMember(@Param("memberId") Long memberId);
}
