package jikgong.domain.wage.repository;

import jikgong.domain.wage.entity.Wage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WageRepository extends JpaRepository<Wage, Long> {
}
