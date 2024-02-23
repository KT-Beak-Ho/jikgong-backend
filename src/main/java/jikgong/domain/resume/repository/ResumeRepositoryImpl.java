package jikgong.domain.resume.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jikgong.domain.common.Address;
import jikgong.domain.headHunting.dtos.HeadHuntingListResponse;
import jikgong.domain.headHunting.entity.SortType;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.location.entity.QLocation;
import jikgong.domain.member.entity.QMember;
import jikgong.domain.resume.entity.QResume;
import jikgong.domain.resume.entity.Resume;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import static jikgong.domain.location.entity.QLocation.*;
import static jikgong.domain.member.entity.QMember.*;
import static jikgong.domain.resume.entity.QResume.*;

@RequiredArgsConstructor
public class ResumeRepositoryImpl implements ResumeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public Page<HeadHuntingListResponse> findHeadHuntingMemberList(Address projectAddress, Tech tech, Float bound, SortType sortType, Pageable pageable) {
        List<Resume> headHuntingList = queryFactory
                .selectFrom(resume)
                .leftJoin(resume.member, member)
                .leftJoin(member.locationList, location).on(location.isMain.isTrue())
                .where(
                        containTech(tech),
                        ltBound(bound, projectAddress, location)
                )
                .orderBy(selectOrderBySpecifier(sortType, projectAddress, location))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(resume.count())
                .from(resume)
                .leftJoin(resume.member, member)
                .leftJoin(member.locationList, location).on(location.isMain.isTrue())
                .where(
                        containTech(tech),
                        ltBound(bound, projectAddress, location)
                )
                .fetchOne();

        List<HeadHuntingListResponse> headHuntingListResponse = headHuntingList.stream()
                .map(h -> HeadHuntingListResponse.from(h, projectAddress))
                .collect(Collectors.toList());

        return new PageImpl<>(headHuntingListResponse, pageable, totalCount);
    }

    private OrderSpecifier<?> selectOrderBySpecifier(SortType sortType, Address projectAddress, QLocation location) {
        // 경력 내림 차순
        if (sortType == SortType.CAREER) {
            return resume.career.desc();
        }
        // 거리 오름 차순
        if (sortType == SortType.DISTANCE) {
            return getDistance(projectAddress, location).asc();
        }

        // 기본 정렬: 거리 오름 차순
        return getDistance(projectAddress, location).asc();
    }

    private BooleanExpression ltBound(Float bound, Address projectAddress, QLocation location) {
        return bound == null ? null : getDistance(projectAddress, location).lt(bound);
    }

    private BooleanExpression containTech(Tech tech) {
        return tech == null ? null : resume.skillList.any().tech.eq(tech);
    }

    private NumberExpression<Double> getDistance(Address projectAddress, QLocation locationParam) {
        // latitude 를 radians 로 계산
        NumberExpression<Double> radiansLatitude =
                Expressions.numberTemplate(Double.class, "radians({0})", projectAddress.getLatitude());

        // 계산된 latitude -> 코사인 계산
        NumberExpression<Double> cosLatitude =
                Expressions.numberTemplate(Double.class, "cos({0})", radiansLatitude);
        NumberExpression<Double> cosSubwayLatitude =
                Expressions.numberTemplate(Double.class, "cos(radians({0}))", locationParam.address.latitude);

        // 계산된 latitude -> 사인 계산
        NumberExpression<Double> sinLatitude =
                Expressions.numberTemplate(Double.class, "sin({0})", radiansLatitude);
        NumberExpression<Double> sinSubWayLatitude =
                Expressions.numberTemplate(Double.class, "sin(radians({0}))", locationParam.address.latitude);

        // 사이 거리 계산
        NumberExpression<Double> cosLongitude =
                Expressions.numberTemplate(Double.class, "cos(radians({0}) - radians({1}))", locationParam.address.longitude, projectAddress.getLongitude());

        NumberExpression<Double> acosExpression =
                Expressions.numberTemplate(Double.class, "acos({0})", cosLatitude.multiply(cosSubwayLatitude).multiply(cosLongitude).add(sinLatitude.multiply(sinSubWayLatitude)));

        // 최종 계산
        NumberExpression<Double> distanceExpression =
                Expressions.numberTemplate(Double.class, "6371 * {0}", acosExpression);

        return distanceExpression;
    }
}
