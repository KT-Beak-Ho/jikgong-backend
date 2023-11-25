package jikgong.domain.pickupLocation.repository;

import jikgong.domain.pickupLocation.entity.PickupLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupLocationRepository extends JpaRepository<PickupLocation, Long> {
}
