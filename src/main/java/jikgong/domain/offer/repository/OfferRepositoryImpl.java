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

import static jikgong.domain.jobpost.entity.QJobPost.*;
import static jikgong.domain.member.entity.QMember.*;
import static jikgong.domain.offer.entity.QOffer.*;

@RequiredArgsConstructor
public class OfferRepositoryImpl implements OfferRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Offer> findOfferHistory(Long companyId, OfferStatus offerStatus, Pageable pageable) {
        List<Offer> result = queryFactory
            .select(offer)
            .from(offer)
            .join(offer.worker, member).fetchJoin()
            .join(offer.jobPost, jobPost).fetchJoin()
            .where(
                eqCompany(companyId),
                eqOfferStatus(offerStatus)
            )
            .orderBy(offer.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long totalCount = queryFactory
            .select(offer.count())
            .from(offer)
            .where(
                eqCompany(companyId),
                eqOfferStatus(offerStatus)
            )
            .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private BooleanExpression eqCompany(Long companyId) {
        return offer.company.id.eq(companyId);
    }

    private BooleanExpression eqOfferStatus(OfferStatus offerStatus) {
        return offerStatus == null ? null : offer.offerStatus.eq(offerStatus);
    }
}
