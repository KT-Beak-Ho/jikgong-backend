package jikgong.domain.headHunting.repository;

import jikgong.domain.common.Address;
import jikgong.domain.headHunting.dtos.HeadHuntingListResponse;
import jikgong.domain.headHunting.entity.HeadHunting;
import jikgong.domain.headHunting.entity.SortType;
import jikgong.domain.jobPost.entity.Tech;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HeadHuntingRepositoryCustom {
    Page<HeadHuntingListResponse> findHeadHuntingMemberList(Address address, Tech tech, Float bound, SortType sortType, Pageable pageable);

}
