package jikgong.domain.offer.repository;

import jikgong.domain.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long>, OfferRepositoryCustom {
    /**
     * find by id and member
     */
    @Query("select o from Offer o where o.company.id = :memberId and o.id = :offerId")
    Optional<Offer> findByIdAndMember(@Param("memberId") Long memberId, @Param("offerId") Long offerId);
}
