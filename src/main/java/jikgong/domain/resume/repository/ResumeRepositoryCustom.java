package jikgong.domain.resume.repository;

import jikgong.domain.common.Address;
import jikgong.domain.offer.entity.SortType;
import jikgong.domain.resume.dto.company.ResumeListResponse;
import jikgong.domain.workexperience.entity.Tech;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResumeRepositoryCustom {

    Page<ResumeListResponse> findHeadHuntingMemberList(Address projectAddress, Tech tech, Float bound,
        SortType sortType, Pageable pageable);
}
