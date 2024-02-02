package jikgong.domain.scrap.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jikgong.domain.jobPost.dtos.worker.JobPostListResponse;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static jikgong.domain.jobPost.entity.QJobPost.*;
import static jikgong.domain.member.entity.QMember.member;
import static jikgong.domain.scrap.entity.QScrap.*;

@RequiredArgsConstructor
public class ScrapRepositoryImpl implements ScrapRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<JobPostListResponse> findByMember(Member worker, Location location, Pageable pageable) {
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
                        Expressions.constant(false)
                ))
                .from(scrap)
                .leftJoin(scrap.jobPost, jobPost)
                .leftJoin(scrap.member, member)
                .where(scrap.member.id.eq(worker.getId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getDistance(location).asc())
                .fetch();

        Long totalCount = queryFactory
                .select(scrap.count())
                .from(scrap)
                .where(scrap.member.id.eq(worker.getId()))
                .fetchOne();

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
}
