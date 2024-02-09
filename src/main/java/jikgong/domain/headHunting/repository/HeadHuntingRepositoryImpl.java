package jikgong.domain.headHunting.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jikgong.domain.common.Address;
import jikgong.domain.headHunting.dtos.HeadHuntingListResponse;
import jikgong.domain.headHunting.entity.QHeadHunting;
import jikgong.domain.headHunting.entity.SortType;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.entity.QLocation;
import jikgong.domain.member.entity.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static jikgong.domain.headHunting.entity.QHeadHunting.*;
import static jikgong.domain.location.entity.QLocation.*;
import static jikgong.domain.member.entity.QMember.*;

@RequiredArgsConstructor
public class HeadHuntingRepositoryImpl implements HeadHuntingRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public Page<HeadHuntingListResponse> findHeadHuntingMemberList(Address projectAddress, Tech tech, Float bound, SortType sortType, Pageable pageable) {
//        queryFactory
//                .select(Projections.constructor(HeadHuntingListResponse.class,
//                        ))
//                .from(headHunting)
//                .join(headHunting.member, member)
//                .join(member.locationList, location).on(location.isMain.isTrue())
//                .where(
//                        eqTech(tech),
//                        ltBound(bound),
//                        getDistance(projectAddress, location)
//                )
        return null;
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
