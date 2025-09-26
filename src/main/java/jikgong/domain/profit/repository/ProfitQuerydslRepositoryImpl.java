package jikgong.domain.profit.repository;

import static jikgong.domain.profit.entity.QProfit.profit;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jikgong.domain.profit.entity.Profit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ProfitQuerydslRepositoryImpl implements ProfitQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Profit> findByWorkDate(Long workerId, LocalDate date) {
        return Optional.ofNullable(queryFactory
                .selectFrom(profit)
                .where(eqWorkerId(workerId),
                        eqWorkDate(date))
                .fetchOne());
    }

    private BooleanExpression eqWorkerId(Long workerId) {
        return workerId == null ? null : profit.member.id.eq(workerId);
    }

    private BooleanExpression eqWorkDate(LocalDate date) {
        return date == null ? null : profit.date.eq(date);
    }
}
