package jikgong.domain.location.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.location.dtos.LocationResponse;
import jikgong.domain.location.dtos.LocationSaveRequest;
import jikgong.domain.location.service.LocationService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LocationController {
    private final LocationService locationService;

    @Operation(summary = "신규 위치 등록")
    @PostMapping("/api/location")
    public ResponseEntity<Response> saveLocation(@RequestBody LocationSaveRequest request,
                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long locationId = locationService.saveLocation(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("신규 위치 등록 완료"));
    }

    @Operation(summary = "등록된 위치 조회")
    @GetMapping("/api/locations")
    public ResponseEntity<Response> findLocationByMember(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<LocationResponse> locationResponseList = locationService.findLocationByMember(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(locationResponseList, "등록된 위치 정보 반환"));
    }

    @Operation(summary = "대표 위치 변경")
    @PutMapping("/api/locations/representative/{locationId}")
    public ResponseEntity<Response> changeMainLocation(@PathVariable("locationId") Long locationId,
                                                       @AuthenticationPrincipal PrincipalDetails principalDetails) {
        locationService.changeMainLocation(principalDetails.getMember().getId(), locationId);
        return ResponseEntity.ok(new Response("대표 위치 변경 완료"));
    }
}
