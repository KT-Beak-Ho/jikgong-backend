package jikgong.domain.offer.repository;

import jikgong.domain.offer.entity.Offer;
import jikgong.domain.offer.entity.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OfferRepositoryCustom {

    Page<Offer> findOfferHistory(Long companyId, OfferStatus offerStatus, Pageable pageable);
}
