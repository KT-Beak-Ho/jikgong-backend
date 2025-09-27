package jikgong.domain.jobpost.repository;

import static jikgong.domain.jobpost.entity.jobpost.QJobPost.jobPost;
import static jikgong.domain.member.entity.QMember.member;
import static jikgong.domain.history.entity.QHistory.history;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.jobpost.entity.jobpost.Park;
import jikgong.domain.jobpost.entity.jobpost.SortType;
import jikgong.domain.location.entity.Location;
import jikgong.domain.workexperience.entity.Tech;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class JobPostQuerydslRepositoryImpl implements JobPostQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<JobPost> findJobPostsByIds(Long workerId, List<Long> jobPostIds) {
        return queryFactory
                .selectFrom(jobPost)
                .where(inIds(jobPostIds),
                        eqWorkerId(workerId))
                .fetch();
    }

    @Override
    public Page<JobPost> getMainPage(Long memberId, List<Tech> techList, List<LocalDate> dateList, Boolean isScrap,
        Boolean meal, Park park, String city, String district, Location location, SortType sortType,
        Pageable pageable) {
        List<JobPost> jobPostList = queryFactory
            .selectFrom(jobPost)
            .leftJoin(jobPost.member, member).fetchJoin()
            .where(
                eqTech(techList),
                eqWorkDate(dateList),
                eqScrap(memberId, isScrap),
                eqMeal(meal),
                eqPark(park),
                eqCity(city),
                eqDistrict(district),
                workDateAfterToday() // 모든 날짜가 과거일 땐 조회 X
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
    public List<JobPost> findJobPostOnMap(Long memberId, Float northEastLat, Float northEastLng, Float southWestLat,
        Float southWestLng, List<Tech> techList, List<LocalDate> dateList, Boolean scrap) {
        return queryFactory
            .selectFrom(jobPost)
            .where(
                jobPost.jobPostAddress.latitude.between(southWestLat, northEastLat)
                    .and(jobPost.jobPostAddress.longitude.between(southWestLng, northEastLng)),
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
            Expressions.numberTemplate(Double.class, "cos(radians({0}))", jobPost.jobPostAddress.latitude);

        // 계산된 latitude -> 사인 계산
        NumberExpression<Double> sinLatitude =
            Expressions.numberTemplate(Double.class, "sin({0})", radiansLatitude);
        NumberExpression<Double> sinSubWayLatitude =
            Expressions.numberTemplate(Double.class, "sin(radians({0}))", jobPost.jobPostAddress.latitude);

        // 사이 거리 계산
        NumberExpression<Double> cosLongitude =
            Expressions.numberTemplate(Double.class, "cos(radians({0}) - radians({1}))",
                jobPost.jobPostAddress.longitude,
                location.getAddress().getLongitude());

        NumberExpression<Double> acosExpression =
            Expressions.numberTemplate(Double.class, "acos({0})",
                cosLatitude.multiply(cosSubwayLatitude).multiply(cosLongitude)
                    .add(sinLatitude.multiply(sinSubWayLatitude)));

        // 최종 계산
        NumberExpression<Double> distanceExpression =
            Expressions.numberTemplate(Double.class, "6371 * {0}", acosExpression);

        return distanceExpression;
    }

    private BooleanExpression eqTech(List<Tech> techList) {
        if (techList == null) {
            return null;
        }
        if (techList.isEmpty()) {
            return null;
        }
        return jobPost.tech.in(techList);
    }

    private BooleanExpression eqWorkDate(List<LocalDate> dateList) {
        return dateList == null ? null : jobPost.workDateList.any().date.in(dateList);
    }

    private BooleanExpression eqScrap(Long memberId, Boolean scrap) {
        if (scrap == null || memberId == null) {
            return null;
        }
        return !scrap ? null : jobPost.scrapList.any().member.id.eq(memberId);
    }

    private BooleanExpression eqMeal(Boolean meal) {
        return meal == null ? null : jobPost.availableInfo.meal.eq(meal);
    }

    private BooleanExpression eqPark(Park park) {
        return park == null ? null : jobPost.availableInfo.park.eq(park);
    }

    private BooleanExpression eqCity(String city) {
        return city == null ? null : jobPost.jobPostAddress.city.eq(city);
    }

    private BooleanExpression eqDistrict(String district) {
        return district == null ? null : jobPost.jobPostAddress.district.eq(district);
    }

    private BooleanExpression eqWorkerId(Long workerId) {
        return workerId == null ? null : JPAExpressions.selectOne()
                .from(history)
                .where(history.member.id.eq(workerId),
                        history.workDate.jobPost.id.eq(jobPost.id))
                .exists();
    }

    private BooleanExpression workDateAfterToday() {
        LocalDate today = LocalDate.now();
        return jobPost.workDateList.any().date.after(today);
    }

    private BooleanExpression inIds(List<Long> ids) {
        return ids == null ? null : jobPost.id.in(ids);
    }

    // 동적 정렬 기준
    private OrderSpecifier<?> selectOrderBySpecifier(SortType sortType, Location location) {
        // 임금 높은 순
        if (sortType == SortType.WAGE || location == null) {
            return jobPost.wage.desc();
        }
        // 가까운 순
        else {
            return getDistance(location).asc();
        }
    }
}