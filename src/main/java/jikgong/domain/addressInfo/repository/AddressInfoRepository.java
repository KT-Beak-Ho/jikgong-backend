package jikgong.domain.addressInfo.repository;

import jikgong.domain.addressInfo.entity.AddressInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressInfoRepository extends JpaRepository<AddressInfo, Long> {
    @Modifying
    @Query("delete from AddressInfo a where a.jobPost.member.id = :memberId and a.jobPost.id = :jobPostId and a.jobPost.isTemporary = true")
    void deleteByMemberAndJobPost(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId);
}
