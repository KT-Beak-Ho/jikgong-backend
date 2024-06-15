//package jikgong.domain.etc.searchLog2;
//
//import jikgong.domain.member.entity.Member;
//import jikgong.domain.member.repository.MemberRepository;
//import jikgong.global.exception.JikgongException;
//import jikgong.global.exception.ErrorCode;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class SearchLog2Service {
//    private final SearchLog2Repository searchLogRepository;
//    private final MemberRepository memberRepository;
//
//    public void saveSearchLog(Long memberId, SearchLogRequest request) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
//
//        SearchLog searchLog = SearchLog.builder()
//                .keyword(request.getKeyword())
//                .member(member)
//                .build();
//
//        // 5개 이상일 때 삭제
//        if(searchLogRepository.cntSearchLog(member.getId()) >= 10) {
//            SearchLog oldestSearchLog = searchLogRepository.findOldestSearchLogId(member.getId());
//            searchLogRepository.delete(oldestSearchLog);
//        }
//
//        searchLogRepository.save(searchLog);
//    }
//}