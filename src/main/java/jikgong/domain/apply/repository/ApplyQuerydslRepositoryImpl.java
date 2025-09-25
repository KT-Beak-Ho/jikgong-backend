package jikgong.domain.apply.repository;

import static jikgong.domain.apply.entity.QApply.apply;
import static jikgong.domain.workdate.entity.QWorkDate.workDate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jikgong.domain.apply.dto.worker.ApplyStatusGetRequest;
import jikgong.domain.apply.entity.Apply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ApplyQuerydslRepositoryImpl implements ApplyQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Apply> findAppliesWithWorkDate(Long workerId, ApplyStatusGetRequest request) {
        return queryFactory
                .select(apply)
                .from(apply)
                .join(apply.workDate, workDate).fetchJoin()
                .where(eqWorkerId(workerId),
                        goeStartWorkDate(request.getStartWorkDate()),
                        loeEndWorkDate(request.getEndWorkDate()))
                .fetch();
    }

    private BooleanExpression eqWorkerId(Long workerId) {
        return workerId == null ? null : apply.member.id.eq(workerId);
    }

    private BooleanExpression goeStartWorkDate(LocalDate startDate) {
        return startDate == null ? null : apply.workDate.date.goe(startDate);
    }

    private BooleanExpression loeEndWorkDate(LocalDate endDate) {
        return endDate == null ? null : apply.workDate.date.loe(endDate);
    }
}
