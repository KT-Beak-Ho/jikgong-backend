package jikgong.domain.searchlog.service;

import java.time.LocalDateTime;
import java.util.List;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.searchlog.dto.SearchLogDeleteRequest;
import jikgong.domain.searchlog.dto.SearchLogSaveRequest;
import jikgong.domain.searchlog.entity.SearchLog;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final RedisTemplate<String, SearchLog> redisTemplate;
    private final MemberRepository memberRepository;

    /**
     * 검색 기록 저장
     */
    public void saveRecentSearchLog(Long memberId, SearchLogSaveRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        String now = LocalDateTime.now().toString();
        String key = "SearchLog" + member.getId();
        SearchLog value = SearchLog.builder()
            .name(request.getName())
            .createdAt(now)
            .build();

        Long size = redisTemplate.opsForList().size(key);
        if (size == 10) {
            // rightPop을 통해 가장 오래된 데이터 삭제
            redisTemplate.opsForList().rightPop(key);
        }

        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 검색 기록 조회
     * 최대 10개
     */
    public List<SearchLog> findRecentSearchLogs(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        String key = "SearchLog" + member.getId();
        List<SearchLog> logs = redisTemplate.opsForList().
            range(key, 0, 10);

        return logs;
    }

    /**
     * 검색 기록 삭제
     */
    public void deleteRecentSearchLog(Long memberId, SearchLogDeleteRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        String key = "SearchLog" + member.getId();
        SearchLog value = SearchLog.builder()
            .name(request.getName())
            .createdAt(request.getCreatedAt())
            .build();

        long count = redisTemplate.opsForList().remove(key, 1, value);

        if (count == 0) {
            throw new JikgongException(ErrorCode.SEARCH_LOG_NOT_EXIST);
        }
    }
}
