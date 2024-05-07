package jikgong.domain.jobPost.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jikgong.domain.jobPost.dtos.worker.JobPostListResponse;
import jikgong.domain.jobPost.entity.*;
import jikgong.domain.location.entity.Location;
import jikgong.domain.scrap.entity.QScrap;
import jikgong.domain.scrap.entity.Scrap;
import jikgong.global.utils.DistanceCal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jikgong.domain.jobPost.entity.QJobPost.*;
import static jikgong.domain.member.entity.QMember.member;
import static jikgong.domain.scrap.entity.QScrap.*;

@RequiredArgsConstructor
@Slf4j
public class JobPostRepositoryImpl implements JobPostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<JobPost> getMainPage(Long memberId, List<Tech> techList, List<LocalDate> dateList, Boolean isScrap, Boolean meal, Park park, Location location, SortType sortType, Pageable pageable) {
        List<JobPost> jobPostList = queryFactory
                .selectFrom(jobPost)
                .leftJoin(jobPost.member, member).fetchJoin()
                .where(
                        eqTech(techList),
                        eqWorkDate(dateList),
                        eqScrap(memberId, isScrap),
                        eqMeal(meal),
                        eqPark(park)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(selectOrderBySpecifier(sortType, location))
                .fetch();

        Long totalCount = queryFactory
                .select(jobPost.count())
                .from(jobPost)
                .where(
                        eqTech(techList),
                        eqWorkDate(dateList),
                        eqScrap(memberId, isScrap),
                        eqMeal(meal),
                        eqPark(park)
                )
                .fetchOne();

        return new PageImpl<>(jobPostList, pageable, totalCount);
    }

    @Override
    public List<JobPost> findJobPostOnMap(Long memberId, Float northEastLat, Float northEastLng, Float southWestLat, Float southWestLng, List<Tech> techList, List<LocalDate> dateList, Boolean scrap) {
        return queryFactory
                .selectFrom(jobPost)
                .where(
                        jobPost.address.latitude.between(southWestLat, northEastLat).and(jobPost.address.longitude.between(southWestLng, northEastLng)),
                        eqTech(techList),
                        eqWorkDate(dateList),
                        eqScrap(memberId, scrap)
                )
                .fetch();
    }

    private NumberExpression<Double> getDistance(Location location) {
        // latitude 를 radians 로 계산
        NumberExpression<Double> radiansLatitude =
                Expressions.numberTemplate(Double.class, "radians({0})", location.getAddress().getLatitude());

        // 계산된 latitude -> 코사인 계산
        NumberExpression<Double> cosLatitude =
                Expressions.numberTemplate(Double.class, "cos({0})", radiansLatitude);
        NumberExpression<Double> cosSubwayLatitude =
                Expressions.numberTemplate(Double.class, "cos(radians({0}))", jobPost.address.latitude);

        // 계산된 latitude -> 사인 계산
        NumberExpression<Double> sinLatitude =
                Expressions.numberTemplate(Double.class, "sin({0})", radiansLatitude);
        NumberExpression<Double> sinSubWayLatitude =
                Expressions.numberTemplate(Double.class, "sin(radians({0}))", jobPost.address.latitude);

        // 사이 거리 계산
        NumberExpression<Double> cosLongitude =
                Expressions.numberTemplate(Double.class, "cos(radians({0}) - radians({1}))", jobPost.address.longitude, location.getAddress().getLongitude());

        NumberExpression<Double> acosExpression =
                Expressions.numberTemplate(Double.class, "acos({0})", cosLatitude.multiply(cosSubwayLatitude).multiply(cosLongitude).add(sinLatitude.multiply(sinSubWayLatitude)));

        // 최종 계산
        NumberExpression<Double> distanceExpression =
                Expressions.numberTemplate(Double.class, "6371 * {0}", acosExpression);

        return distanceExpression;
    }

    private BooleanExpression eqTech(List<Tech> techList) {
        return techList == null ? null : jobPost.tech.in(techList);
    }

    private BooleanExpression eqWorkDate(List<LocalDate> dateList) {
        return dateList == null ? null : jobPost.workDateList.any().date.in(dateList);
    }

    private BooleanExpression eqScrap(Long memberId, Boolean scrap) {
        if (scrap == null) return null;
        return !scrap ? null : jobPost.scrapList.any().member.id.eq(memberId);
    }

    private BooleanExpression eqMeal(Boolean meal) {
        return meal == null ? null : jobPost.availableInfo.meal.eq(meal);
    }

    private BooleanExpression eqPark(Park park) {
        return park == null ? null : jobPost.availableInfo.park.eq(park);
    }

    // 동적 정렬 기준
    private OrderSpecifier<?> selectOrderBySpecifier(SortType sortType, Location location) {
        // 임금 높은 순 (기본)
        if (sortType == SortType.WAGE) {
            return jobPost.wage.desc();
        }
        // 가까운 순
        else {
            return getDistance(location).asc();
        }
    }
}