package jikgong.domain.offerWorkDate.repository;

import jikgong.domain.offer.entity.Offer;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.offerWorkDate.entity.OfferWorkDateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OfferWorkDateRepository extends JpaRepository<OfferWorkDate, Long> {
    @Query("select ow from OfferWorkDate ow join fetch ow.offer.jobPost j join fetch ow.offer o join fetch ow.workDate w " +
            "where o.worker.id = :workerId and ow.offerWorkDateStatus = 'OFFER_PENDING' and o.offerStatus = 'OFFER'")
    List<OfferWorkDate> findReceivedPendingOffer(@Param("workerId") Long workerId);

    @Query("select ow from OfferWorkDate ow join fetch ow.offer.jobPost j join fetch ow.offer o join fetch ow.workDate w " +
            "where o.worker.id = :workerId and ow.offerWorkDateStatus != 'OFFER_PENDING'")
    List<OfferWorkDate> findReceivedClosedOffer(@Param("workerId") Long workerId);

    @Modifying
    @Query("update OfferWorkDate ow set ow.offerWorkDateStatus = :status where ow.offer.id = :offerId")
    int cancelOffer(@Param("offerId") Long offerId, @Param("status") OfferWorkDateStatus status);
}
