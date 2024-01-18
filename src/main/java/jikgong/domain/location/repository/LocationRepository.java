package jikgong.domain.location.repository;

import jikgong.domain.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("select l from Location l where l.member.id = :memberId")
    List<Location> findByMemberId(@Param("memberId") Long memberId);

    @Query("select l from Location l where l.member.id = :memberId and l.isMain = true")
    Optional<Location> findMainLocationByMemberId(@Param("memberId") Long memberId);

    @Query("select l from Location l where l.member.id = :memberId and l.id = :locationId")
    Optional<Location> findByLocationIdAndMemberId(@Param("memberId") Long memberId, @Param("locationId") Long locationId);

    @Modifying
    @Query("delete from Location l where l.member.id = :memberId and l.id = :locationId")
    void deleteByLocationIdAndMemberId(@Param("memberId") Long memberId, @Param("locationId") Long locationId);

    @Modifying
    @Query("delete from Location l where l.member.id = :memberId and l.id in :locationIdList")
    void deleteByIdList(@Param("memberId") Long memberId, @Param("locationIdList") List<Long> locationIdList);
}
