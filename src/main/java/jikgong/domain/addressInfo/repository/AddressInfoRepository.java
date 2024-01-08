package jikgong.domain.addressInfo.repository;

import jikgong.domain.addressInfo.entity.AddressInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressInfoRepository extends JpaRepository<AddressInfo, Long> {
}
