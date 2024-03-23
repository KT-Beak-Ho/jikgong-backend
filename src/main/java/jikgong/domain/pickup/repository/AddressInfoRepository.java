package jikgong.domain.pickup.repository;

import jikgong.domain.pickup.entity.Pickup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressInfoRepository extends JpaRepository<Pickup, Long> {
}
