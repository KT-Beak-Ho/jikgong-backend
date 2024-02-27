package jikgong.domain.offer.repository;

import jikgong.domain.offer.dtos.OfferHistoryResponse;
import jikgong.domain.offer.entity.Offer;
import jikgong.domain.offer.entity.OfferStatus;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OfferRepositoryCustom {
    Page<Offer> findOfferHistory(Long companyId, OfferStatus offerStatus, Pageable pageable);
}
