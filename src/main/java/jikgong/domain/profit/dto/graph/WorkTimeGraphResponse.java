package jikgong.domain.profit.dto.graph;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class WorkTimeGraphResponse {

    private long totalWorkTime;
    private List<String> workTimeList;

    public void plusTime(LocalTime startTime, LocalTime endTime) {
        Duration workDuration = Duration.between(startTime, endTime);
        this.totalWorkTime += workDuration.toMinutes();
    }

    public void addWorkTime(LocalTime startTime, LocalTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedStartTime = startTime.format(formatter);
        String formattedEndTime = endTime.format(formatter);
        String workTime = formattedStartTime + " ~ " + formattedEndTime;
        this.workTimeList.add(workTime);
    }

    public static WorkTimeGraphResponse createDto(LocalTime startTime, LocalTime endTime) {
        long totalWorkTime = Duration.between(startTime, endTime).toMinutes();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedStartTime = startTime.format(formatter);
        String formattedEndTime = endTime.format(formatter);
        String workTime = formattedStartTime + " ~ " + formattedEndTime;

        return WorkTimeGraphResponse.builder()
            .totalWorkTime(totalWorkTime)
            .workTimeList(new ArrayList<>(List.of(workTime)))
            .build();
    }
}
