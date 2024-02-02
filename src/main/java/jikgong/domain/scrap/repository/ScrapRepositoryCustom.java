package jikgong.domain.scrap.repository;

import jikgong.domain.jobPost.dtos.worker.JobPostListResponse;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Member;
import jikgong.domain.scrap.entity.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScrapRepositoryCustom {
    Page<JobPostListResponse> findByMember(Member member, Location location, Pageable pageable);
}
