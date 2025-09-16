package jikgong.domain.location.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.location.dto.*;
import jikgong.domain.location.service.LocationSearchService;
import jikgong.domain.location.service.LocationService;
import jikgong.global.annotation.WorkerRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="[공통] 위치 정보")
@RestController
@RequiredArgsConstructor
@Slf4j
@WorkerRoleRequired
public class LocationController {

    private final LocationService locationService;
    private final LocationSearchService locationSearchService;

    @Operation(summary = "신규 위치 등록")
    @PostMapping("/api/location")
    public ResponseEntity<Response> saveLocation(@RequestBody LocationSaveRequest request,
        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long locationId = locationService.saveLocation(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("신규 위치 등록 완료"));
    }

    @Operation(summary = "등록된 위치 조회")
    @GetMapping("/api/location/list")
    public ResponseEntity<Response> findLocationByMember(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<LocationResponse> locationResponseList = locationService.findLocationByMember(
            principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(locationResponseList, "등록된 위치 정보 반환 완료"));
    }

    @Operation(summary = "등록한 위치 수정")
    @PutMapping("/api/location/{locationId}")
    public ResponseEntity<Response> updateLocation(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody LocationUpdateRequest request) {
        locationService.updateLocation(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("위치 정보 수정 완료"));
    }

    @Operation(summary = "위치 삭제 (단일)")
    @DeleteMapping("/api/location/{locationId}")
    public ResponseEntity<Response> deleteLocation(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("locationId") Long locationId) {
        locationService.deleteLocation(principalDetails.getMember().getId(), locationId);
        return ResponseEntity.ok(new Response("위치 정보 삭제 완료"));
    }

    @Operation(summary = "위치 삭제 (복수)")
    @DeleteMapping("/api/location/list")
    public ResponseEntity<Response> deleteLocations(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody LocationDeleteRequest request) {
        locationService.deleteLocations(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("위치 정보 삭제 완료"));
    }

    @Operation(summary = "대표 위치 변경 및 설정", description = "기존에 대표 위치로 설정 해둔게 있다면 변경, 없다면 설정")
    @PostMapping("/api/location/representative/{locationId}")
    public ResponseEntity<Response> changeMainLocation(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("locationId") Long locationId) {
        locationService.changeMainLocation(principalDetails.getMember().getId(), locationId);
        return ResponseEntity.ok(new Response("대표 위치 변경 완료"));
    }

    @Operation(summary = "위치 후보 검색")
    @GetMapping("/api/location/candidate/list")
    public ResponseEntity<Response> searchLocationCandidates(@RequestParam String keyword) {
        List<LocationSearchResponse> locationSearchResponseList = locationSearchService.searchLocationCandidatesByKeyword(keyword);
        return ResponseEntity.ok(new Response(locationSearchResponseList, "위치 후보 정보 반환 완료"));
    }
}
