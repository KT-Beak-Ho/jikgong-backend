package jikgong.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.jobPost.entity.SortType;
import jikgong.domain.member.service.HeadHuntingService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
                                                        @RequestParam(name = "bound") Float bound,
                                                        @RequestParam(name = "sortType") SortType sortType,
                                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        headHuntingService.findHeadHuntingList(principalDetails.getMember().getId(), projectId, bound, sortType, page, size);
        return ResponseEntity.ok(new Response("헤드 헌팅: 노동자 리스트 반환"));
    }
}
