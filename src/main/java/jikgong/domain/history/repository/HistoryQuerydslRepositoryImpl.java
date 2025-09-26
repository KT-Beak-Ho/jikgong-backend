package jikgong.domain.history.repository;

import static jikgong.domain.history.entity.QHistory.history;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jikgong.domain.history.entity.History;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class HistoryQuerydslRepositoryImpl implements HistoryQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<History> findByWorkDateForWorker(Long workerId, LocalDate date) {
        return Optional.ofNullable(queryFactory
                .selectFrom(history)
                .where(eqMemberId(workerId),
                        eqWorkDate(date))
                .fetchOne());
    }

    @Override
    public List<History> findByWorkDateForCompany(Long companyId, LocalDate date) {
        return queryFactory
                .selectFrom(history)
                .where(eqMemberId(companyId),
                        eqWorkDate(date))
                .fetch();
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return memberId == null ? null : history.member.id.eq(memberId);
    }

    private BooleanExpression eqWorkDate(LocalDate date) {
        return date == null ? null : history.workDate.date.eq(date);
    }
}
