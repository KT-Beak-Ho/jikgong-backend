package jikgong.domain.workdate.dto;

import lombok.Data;

@Data
public class JobStatisticsByProject {
    private final Integer totalJobs;
    private final Integer totalWorkers;
    private final Integer activeJobs;

    public JobStatisticsByProject(Long totalJobs, Long totalWorkers) {
        this.totalJobs = (totalJobs == null ? 0 : totalJobs.intValue());
        this.totalWorkers = (totalWorkers == null ? 0 : totalWorkers.intValue());
        this.activeJobs = this.totalJobs - this.totalWorkers;
    }
}
