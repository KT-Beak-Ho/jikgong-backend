package jikgong.domain.offer.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jikgong.domain.offer.entity.Offer;
import jikgong.domain.offer.entity.OfferStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static jikgong.domain.jobPost.entity.QJobPost.*;
import static jikgong.domain.member.entity.QMember.*;
import static jikgong.domain.offer.entity.QOffer.*;
import static jikgong.domain.offerWorkDate.entity.QOfferWorkDate.*;

@RequiredArgsConstructor
public class OfferRepositoryImpl implements OfferRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Offer> findOfferHistory(Long companyId, OfferStatus offerStatus, Pageable pageable) {
        List<Offer> result = queryFactory
                .select(offer)
                .from(offer)
                .join(offer.offerWorkDateList, offerWorkDate)
                .join(offer.worker, member)
                .join(offerWorkDate.workDate.jobPost, jobPost)
                .where(eqOfferStatus(offerStatus))
                .orderBy(offer.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(offer.count())
                .from(offer)
                .where(eqOfferStatus(offerStatus))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private BooleanExpression eqOfferStatus(OfferStatus offerStatus) {
        return offerStatus == null ? null : offer.offerStatus.eq(offerStatus);
    }
}
