package jikgong.domain.resume.repository;

import jikgong.domain.common.Address;
import jikgong.domain.headHunting.dtos.HeadHuntingListResponse;
import jikgong.domain.headHunting.entity.SortType;
import jikgong.domain.jobPost.entity.Tech;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResumeRepositoryCustom {
    Page<HeadHuntingListResponse> findHeadHuntingMemberList(Address projectAddress, Tech tech, Float bound, SortType sortType, Pageable pageable);
}
