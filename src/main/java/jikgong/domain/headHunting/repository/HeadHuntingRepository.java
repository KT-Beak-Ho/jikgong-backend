package jikgong.domain.headHunting.repository;

import jikgong.domain.headHunting.entity.HeadHunting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeadHuntingRepository extends JpaRepository<HeadHunting, Long>, HeadHuntingRepositoryCustom {
}
