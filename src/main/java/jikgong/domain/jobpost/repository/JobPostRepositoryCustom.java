package jikgong.domain.jobpost.repository;

import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Park;
import jikgong.domain.jobpost.entity.SortType;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.location.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface JobPostRepositoryCustom {

    Page<JobPost> getMainPage(Long memberId, List<Tech> techList, List<LocalDate> workDateList, Boolean scrap,
        Boolean meal, Park park, Location location, SortType sortType, Pageable pageable);

    List<JobPost> findJobPostOnMap(Long memberId, Float northEastLat, Float northEastLng, Float southWestLat,
        Float southWestLng, List<Tech> techList, List<LocalDate> dateList, Boolean scrap);
}
