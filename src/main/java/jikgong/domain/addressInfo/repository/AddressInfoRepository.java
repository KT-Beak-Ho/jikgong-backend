package jikgong.domain.addressInfo.repository;

import jikgong.domain.addressInfo.entity.AddressInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AddressInfoRepository extends JpaRepository<AddressInfo, Long> {
}
