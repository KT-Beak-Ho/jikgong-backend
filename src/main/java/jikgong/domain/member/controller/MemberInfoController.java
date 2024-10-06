package jikgong.domain.member.controller;


import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import jikgong.domain.member.dto.company.CompanySearchResponse;
import jikgong.domain.member.dto.info.CompanyInfoRequest;
import jikgong.domain.member.dto.info.CompanyInfoResponse;
import jikgong.domain.member.dto.info.PasswordRequest;
import jikgong.domain.member.dto.info.WorkerInfoRequest;
import jikgong.domain.member.dto.info.WorkerInfoResponse;
import jikgong.domain.member.service.MemberInfoService;
import jikgong.global.annotation.AuthenticatedRequired;
import jikgong.global.annotation.CompanyRoleRequired;
import jikgong.global.annotation.WorkerRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberInfoController {

    private final MemberInfoService memberInfoService;

    @Operation(summary = "회원 정보 조회 (노동자)")
    @GetMapping("/api/member-info/worker")
    @WorkerRoleRequired
    public ResponseEntity<Response> findWorkerInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        WorkerInfoResponse workerInfoResponse = memberInfoService.findWorkerInfo(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(workerInfoResponse, "노동자 정보 조회 완료"));
    }

    @Operation(summary = "회원 정보 수정 (노동자)")
    @PutMapping("/api/member-info/worker")
    @WorkerRoleRequired
    public ResponseEntity<Response> updateWorkerInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody WorkerInfoRequest request) {
        memberInfoService.updateWorkerInfo(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("노동자 정보 수정 완료"));
    }

    @Operation(summary = "회원 정보 조회 (기업)")
    @GetMapping("/api/member-info/company")
    @CompanyRoleRequired
    public ResponseEntity<Response> findCompanyInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        CompanyInfoResponse companyInfoResponse = memberInfoService.findCompanyInfo(
            principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(companyInfoResponse, "노동자 정보 조회 완료"));
    }

    @Operation(summary = "회원 정보 수정 (기업)")
    @PutMapping("/api/member-info/company")
    @CompanyRoleRequired
    public ResponseEntity<Response> findCompanyInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody CompanyInfoRequest request) {
        memberInfoService.updateCompanyInfo(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("노동자 정보 수정 완료"));
    }

    @Operation(summary = "비밀번호 변경", description = "회원 정보 수정 시 비밀번호 체크 및 수정")
    @PostMapping("/api/member-info/password-validation")
    @AuthenticatedRequired
    public ResponseEntity<Response> validationPassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody PasswordRequest request) {
        memberInfoService.updatePassword(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("비밀번호 확인 완료"));
    }

    @Operation(summary = "비밀번호 임시 발급", description = "sms로 임시 비밀번호 발급")
    @PostMapping("/api/member-info/password-reset")
    public ResponseEntity<Response> validationPassword(@AuthenticationPrincipal PrincipalDetails principalDetails)
        throws Exception {
        memberInfoService.sendTemporaryPassword(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response("sms로 임시 비밀번호 발급 및 비밀번호 업데이트 완료"));
    }

    // todo: 비밀번호 찾기 개발

    @Operation(summary = "체류 만료일 불러오기", description = "codef api를 활용하여 체류 만료일 정보 저장")
    @PostMapping("/api/member-info/visaExpiryDate")
    @WorkerRoleRequired
    public ResponseEntity<Response> updateVisaExpiryDate(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        // todo: 개발 필요
        return ResponseEntity.ok(new Response("개발 중"));
    }

    @Operation(summary = "기업 검색")
    @GetMapping("/api/member-info/search/company")
    public ResponseEntity<Response> searchCompany(@RequestParam(name = "keyword", required = false) String keyword) {
        List<CompanySearchResponse> companySearchResponseList = memberInfoService.searchCompany(keyword);
        return ResponseEntity.ok(new Response(companySearchResponseList, "기업 검색 결과 반환 완료"));
    }

}
