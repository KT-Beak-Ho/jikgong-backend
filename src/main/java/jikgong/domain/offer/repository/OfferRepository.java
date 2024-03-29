package jikgong.domain.offer.repository;

import jikgong.domain.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long>, OfferRepositoryCustom {
}
