package jikgong.domain.headHunting.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.headHunting.entity.SortType;
import jikgong.domain.headHunting.service.HeadHuntingService;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HeadHuntingController {
    private final HeadHuntingService headHuntingService;

    @Operation(summary = "헤드 헌팅: 노동자 조회")
    @GetMapping("/api/head-hunting/{projectId}")
    public ResponseEntity<Response> findHeadHuntingList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                        @PathVariable("projectId") Long projectId,
                                                        @RequestParam(name = "tech", required = false) Tech tech,
                                                        @RequestParam(name = "bound", required = false) Float bound,
                                                        @RequestParam(name = "sortType", required = false) SortType sortType,
                                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        headHuntingService.findHeadHuntingList(principalDetails.getMember().getId(), projectId, tech, bound, sortType, pageable);
        return ResponseEntity.ok(new Response("헤드 헌팅: 노동자 리스트 반환"));
    }
}
