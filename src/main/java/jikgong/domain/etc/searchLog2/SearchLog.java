//package jikgong.domain.searchLog2;
//
//import jakarta.persistence.*;
//import jikgong.domain.common.BaseEntity;
//import jikgong.domain.member.entity.Member;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Builder
//public class SearchLog extends BaseEntity {
//    @Id
//    @GeneratedValue
//    @Column(name = "search_log_id")
//    private Long id;
//    private String keyword;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;
//}
