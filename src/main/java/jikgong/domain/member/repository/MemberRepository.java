package jikgong.domain.member.repository;

import java.util.List;
import java.util.Optional;
import jikgong.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 회원 가입
     */
    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByPhone(String username);


    /**
     * history service (1차 캐싱)
     */
    @Query("select m from Member m where m.id in :memberIdList")
    List<Member> findByIdList(@Param("memberIdList") List<Long> memberIdList);


    /**
     * 기업 검색
     */
    @Query("select m from Member m where m.companyInfo.companyName like %:keyword% and m.role = 'ROLE_COMPANY'")
    List<Member> findByCompanyName(@Param("keyword") String keyword);

    /**
     * 아이디 찾기
     */
    @Query("select m from Member m where m.phone = :phone and " +
        "(m.workerInfo.workerName = :name or m.companyInfo.companyName = :name)")
    Optional<Member> findMemberForForgottenLoginId(@Param("phone") String phone, @Param("name") String name);

    /**
     * 비밀번호 찾기
     */
    @Query("select m from Member m where m.loginId = :loginId and m.phone = :phone")
    Optional<Member> findMemberForForgottenPassword(@Param("loginId") String loginId, @Param("phone") String phone);
}
