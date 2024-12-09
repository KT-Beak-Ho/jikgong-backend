package jikgong.domain.jobpost.repository;

import java.time.LocalDate;
import java.util.List;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.jobpost.entity.jobpost.Park;
import jikgong.domain.jobpost.entity.jobpost.SortType;
import jikgong.domain.location.entity.Location;
import jikgong.domain.workexperience.entity.Tech;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostRepositoryCustom {

    Page<JobPost> getMainPage(Long memberId, List<Tech> techList, List<LocalDate> workDateList, Boolean scrap,
        Boolean meal, Park park, String city, String district, Location location, SortType sortType, Pageable pageable);

    List<JobPost> findJobPostOnMap(Long memberId, Float northEastLat, Float northEastLng, Float southWestLat,
        Float southWestLng, List<Tech> techList, List<LocalDate> dateList, Boolean scrap);
}
