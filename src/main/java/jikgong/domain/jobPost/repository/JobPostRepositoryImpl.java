package jikgong.domain.jobPost.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jikgong.domain.jobPost.dtos.worker.JobPostListResponse;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.SortType;
import jikgong.domain.jobPost.entity.Tech;
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
    public Page<JobPostListResponse> getMainPage(Long memberId, Tech tech, List<LocalDate> workDateList, Boolean isScrap, Boolean meal, Park park, Location location, SortType sortType, Pageable pageable) {
        List<JobPostListResponse> jobPostList = queryFactory
                .select(Projections.constructor(JobPostListResponse.class,
                        jobPost.id,
                        jobPost.tech,
                        jobPost.recruitNum,
                        jobPost.title,
                        jobPost.availableInfo.meal,
                        jobPost.availableInfo.pickup,
                        jobPost.availableInfo.park,
                        jobPost.startDate,
                        jobPost.endDate,
                        jobPost.startTime,
                        jobPost.endTime,
                        jobPost.address.address,
                        getDistance(location),
                        member.companyInfo.companyName,
                        jobPost.wage,
                        Expressions.constant(false)))
                .from(jobPost)
                .leftJoin(jobPost.member, member)
                .where(
                        eqTech(tech),
                        eqWorkDate(workDateList),
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
                        eqTech(tech),
                        eqWorkDate(workDateList),
                        eqScrap(memberId, isScrap),
                        eqMeal(meal),
                        eqPark(park)
                )
                .fetchOne();

        List<Long> scrapJobPostId = queryFactory
                .select(jobPost.id)
                .from(scrap)
                .leftJoin(scrap.jobPost, jobPost)
                .where(scrap.member.id.eq(memberId))
                .fetch();

        Set<Long> scrapJobPostIdSet = new HashSet<>(scrapJobPostId);

        // 스크랩 여부
        for (JobPostListResponse jobPostListResponse : jobPostList) {
            if (scrapJobPostIdSet.contains(jobPostListResponse.getJobPostId())) {
                jobPostListResponse.setScrap(true);
            }
        }

        return new PageImpl<>(jobPostList, pageable, totalCount);
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

    private BooleanExpression eqTech(Tech tech) {
        return tech == null ? null : jobPost.tech.eq(tech);
    }

    private BooleanExpression eqWorkDate(List<LocalDate> workDateList) {
        return workDateList == null ? null : jobPost.workDateList.any().workDate.in(workDateList);
    }

    private BooleanExpression eqScrap(Long memberId, Boolean scrap) {
        return scrap == null ? null : jobPost.scrapList.any().member.id.eq(memberId);
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
