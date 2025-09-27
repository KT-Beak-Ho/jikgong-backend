package jikgong.domain.history.repository;

import static jikgong.domain.history.entity.QHistory.history;
import static jikgong.domain.workdate.entity.QWorkDate.workDate;
import static jikgong.domain.jobpost.entity.jobpost.QJobPost.jobPost;
import static jikgong.domain.member.entity.QMember.member;

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

    @Override
    public Optional<History> findByIdAndCompanyId(Long historyId, Long companyId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(history)
                .join(history.workDate, workDate).fetchJoin()
                .join(workDate.jobPost, jobPost).fetchJoin()
                .join(jobPost.member, member).fetchJoin()
                .where(eqHistoryId(historyId),
                        eqCompanyId(companyId))
                .fetchOne());
    }

    private BooleanExpression eqHistoryId(Long id) {
        return id == null ? null : history.id.eq(id);
    }

    private BooleanExpression eqWorkerId(Long workerId) {
        return workerId == null ? null : history.member.id.eq(workerId);
    }

    private BooleanExpression eqCompanyId(Long companyId) {
        return companyId == null ? null : workDate.jobPost.member.id.eq(companyId);
    }

    private BooleanExpression eqWorkDateId(Long workDateId) {
        return workDateId == null ? null : history.workDate.id.eq(workDateId);
    }

    private BooleanExpression eqWorkDate(LocalDate date) {
        return date == null ? null : history.workDate.date.eq(date);
    }
}
