package jikgong.domain.member.repository;

import jikgong.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByPhone(String username);

    @Query("select m from Member m where m.id in :memberIdList")
    List<Member> findByIdList(@Param("memberIdList") List<Long> memberIdList);
}
