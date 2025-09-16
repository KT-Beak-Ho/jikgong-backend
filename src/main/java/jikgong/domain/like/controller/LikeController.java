package jikgong.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.like.service.LikeService;
import jikgong.global.annotation.CompanyRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="[사업자] 좋아요")
@RestController
@RequiredArgsConstructor
@Slf4j
@CompanyRoleRequired
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 저장")
    @PostMapping("/api/like/{receiverId}")
    public ResponseEntity<Response> saveLike(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("receiverId") Long receiverId) {
        Long likeId = likeService.saveLike(principalDetails.getMember().getId(), receiverId);
        return ResponseEntity.ok(new Response("좋아요 저장 완료"));
    }

    @Operation(summary = "좋아요 취소")
    @DeleteMapping("/api/like/{receiverId}")
    public ResponseEntity<Response> deleteLike(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("receiverId") Long receiverId) {
        likeService.deleteLike(principalDetails.getMember().getId(), receiverId);
        return ResponseEntity.ok(new Response("좋아요 취소 완료"));
    }
}
