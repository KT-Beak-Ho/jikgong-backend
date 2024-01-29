package jikgong.domain.jobPost.repository;

import jikgong.domain.jobPost.dtos.worker.JobPostListResponse;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.location.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface JobPostRepositoryCustom {
    Page<JobPostListResponse> getMainPage(Long memberId, Tech tech, List<LocalDate> workDateList, Boolean scrap, Boolean meal, Park park, Location location, Pageable pageable);
}
