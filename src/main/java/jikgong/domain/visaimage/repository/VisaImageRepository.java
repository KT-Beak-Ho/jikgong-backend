package jikgong.domain.visaimage.repository;

import jikgong.domain.visaimage.entity.VisaImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisaImageRepository extends JpaRepository<VisaImage, Long> {

}
