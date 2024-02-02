package jikgong.global.utils;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.QJobPost;
import jikgong.domain.location.entity.Location;

public class DistanceCal {
    public static Double getDistance(JobPost jobPost, Location location) {
        double earthRadius = 6371; // 지구 반지름

        // 노동자 위치
        double lat1 = Math.toRadians(location.getAddress().getLatitude());
        double lon1 = Math.toRadians(location.getAddress().getLongitude());

        // 일자리 위치
        double lat2 = Math.toRadians(jobPost.getAddress().getLatitude());
        double lon2 = Math.toRadians(jobPost.getAddress().getLongitude());

        // 위도 및 경도 간의 차이 계산
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Haversine 공식 계산
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리 계산
        double distance = earthRadius * c;

        return distance;
    }
}
