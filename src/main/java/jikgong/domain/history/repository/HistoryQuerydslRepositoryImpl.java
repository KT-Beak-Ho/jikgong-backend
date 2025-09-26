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
                .where(eqWorkerId(workerId),
                        eqWorkDate(date))
                .fetchOne());
    }

    @Override
    public List<History> findByWorkDateId(Long workDateId) {
        return queryFactory
                .selectFrom(history)
                .where(eqWorkDateId(workDateId))
                .fetch();
    }

    private BooleanExpression eqWorkerId(Long workerId) {
        return workerId == null ? null : history.member.id.eq(workerId);
    }

    private BooleanExpression eqWorkDateId(Long workDateId) {
        return workDateId == null ? null : history.workDate.id.eq(workDateId);
    }

    private BooleanExpression eqWorkDate(LocalDate date) {
        return date == null ? null : history.workDate.date.eq(date);
    }

}
