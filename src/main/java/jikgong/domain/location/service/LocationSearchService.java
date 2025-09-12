package jikgong.domain.location.service;

import jikgong.domain.location.dto.KakaoApiResponse;
import jikgong.domain.location.dto.LocationSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@Transactional
@Slf4j
public class LocationSearchService {
    @Value("${kakao.api-key}")
    private String kakaoApiKey;

    public List<LocationSearchResponse> searchLocationCandidatesByKeyword(String keyword) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        // 요청 엔티티 생성
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        // URI 생성
        URI targetUrl = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("page", 1)
                .queryParam("size", 15)
                .queryParam("sort", "accuracy")
                .queryParam("query", keyword)
                .build()
                .encode()
                .toUri();

        // API 호출 및 반환
        return restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, KakaoApiResponse.class)
                .getBody()
                .getDocuments()
                .stream()
                    .filter(doc -> !doc.getRoadAddressName().isEmpty())
                    .map(doc -> {
                        return LocationSearchResponse.builder()
                                .address(doc.getLotNumberAddressName())
                                .road_address(doc.getRoadAddressName())
                                .longitude(doc.getLongitude())
                                .latitude(doc.getLatitude())
                                .build();
                    })
                    .toList();
    }
}
