//package jikgong.domain.searchLog2;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface SearchLog2Repository extends JpaRepository<SearchLog, Long> {
//    @Query("select count(s) from SearchLog s where s.member.id = :memberId")
//    int cntSearchLog(@Param("memberId") Long memberId);
//
//    @Modifying
//    @Query("delete from SearchLog s where s.id = (select s2.id from SearchLog s2 where s2.member.id = :memberId order by s2.createdDate asc limit 1)")
//    void deleteOldestSearchLog(@Param("memberId") Long memberId);
//
//    @Query("select s from SearchLog s where s.member.id = :memberId order by s.createdDate asc limit 1")
//    SearchLog findOldestSearchLogId(@Param("memberId") Long memberId);
//}
