package jikgong.domain.offerWorkDate.repository;

import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferWorkDateRepository extends JpaRepository<OfferWorkDate, Long> {
}
