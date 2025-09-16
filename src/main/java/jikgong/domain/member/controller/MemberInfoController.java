package jikgong.domain.member.controller;


import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.member.dto.company.CompanySearchResponse;
import jikgong.domain.member.dto.info.AccountInfoRequest;
import jikgong.domain.member.dto.info.AccountInfoResponse;
import jikgong.domain.member.dto.info.AuthCodeForFindRequest;
import jikgong.domain.member.dto.info.CompanyInfoRequest;
import jikgong.domain.member.dto.info.CompanyInfoResponse;
import jikgong.domain.member.dto.info.LoginIdFindRequest;
import jikgong.domain.member.dto.info.LoginIdFindResponse;
import jikgong.domain.member.dto.info.PasswordFindRequest;
import jikgong.domain.member.dto.info.PasswordFindResponse;
import jikgong.domain.member.dto.info.PasswordUpdateRequest;
import jikgong.domain.member.dto.info.WorkerCardRequest;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name="[공통] 회원 정보 조회")
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

    @Operation(summary = "계좌 정보 수정 (노동자)")
    @PutMapping("/api/member-info/worker/account")
    @WorkerRoleRequired
    public ResponseEntity<Response> updateWorkerAccountInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody AccountInfoRequest request) {
        memberInfoService.updateWorkerAccount(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("계좌 정보 수정 완료"));
    }

    @Operation(summary = "계좌 정보 조회 (노동자)")
    @GetMapping("/api/member-info/worker/account")
    @WorkerRoleRequired
    public ResponseEntity<Response> getWorkerAccountInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        AccountInfoResponse accountInfoResponse = memberInfoService.getWorkerAccountInfo(
            principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(accountInfoResponse, "계좌 정보 조회 완료"));
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
    public ResponseEntity<Response> updatePassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody PasswordUpdateRequest request) {
        memberInfoService.updatePassword(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("비밀번호 확인 완료"));
    }

    @Operation(summary = "아이디 찾기 전 본인 인증")
    @PostMapping("/api/member-info/loginId-verification")
    public ResponseEntity<Response> verificationBeforeFindLoginId(@RequestBody LoginIdFindRequest request) {
        memberInfoService.verificationBeforeFindLoginId(request);
        return ResponseEntity.ok(new Response("아이디 찾기 인증 코드 발송 완료"));
    }

    @Operation(summary = "아이디 찾기", description = "본인 인증으로 발송된 authCode를 사용하여 아이디 찾기")
    @PostMapping("/api/member-info/loginId/retrieve")
    public ResponseEntity<Response> findLoginId(@RequestBody AuthCodeForFindRequest request) {
        LoginIdFindResponse loginIdFindResponse = memberInfoService.findLoginId(request);
        return ResponseEntity.ok(new Response(loginIdFindResponse, "비밀번호 확인 완료"));
    }

    @Operation(summary = "비밀번호 찾기 전 본인 인증")
    @PostMapping("/api/member-info/password-verification")
    public ResponseEntity<Response> verificationBeforeFindPassword(@RequestBody PasswordFindRequest request)
        throws Exception {
        memberInfoService.verificationBeforeFindPassword(request);
        return ResponseEntity.ok(new Response("비밀번호 찾기 인증 코드 발송 완료"));
    }

    @Operation(summary = "비밀번호 임시 발급", description = "문자 인증 코드 입력 후 임시 비밀번호 발급")
    @PostMapping("/api/member-info/password-temporary")
    public ResponseEntity<Response> updateTemporaryPassword(@RequestBody AuthCodeForFindRequest request)
        throws Exception {
        PasswordFindResponse passwordFindResponse = memberInfoService.updateTemporaryPassword(request);
        return ResponseEntity.ok(new Response(passwordFindResponse, "임시 비밀번호로 업데이트 및 반환 완료"));
    }

    @Operation(summary = "기업 검색")
    @GetMapping("/api/member-info/search/company")
    public ResponseEntity<Response> searchCompany(@RequestParam(name = "keyword", required = false) String keyword) {
        List<CompanySearchResponse> companySearchResponseList = memberInfoService.searchCompany(keyword);
        return ResponseEntity.ok(new Response(companySearchResponseList, "기업 검색 결과 반환 완료"));
    }

    @Operation(summary = "교육 이수증 수정")
    @PutMapping(value = "/api/member-info/educationCertificate", consumes = {"multipart/form-data"})
    @WorkerRoleRequired
    public ResponseEntity<Response> updateEducationCertificate(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestParam(name = "educationCertificateImage") MultipartFile educationCertificateImage) {
        memberInfoService.updateEducationCertificate(principalDetails.getMember().getId(), educationCertificateImage);
        return ResponseEntity.ok(new Response("교육 이수증 수정 완료"));
    }

    @Operation(summary = "근로자 카드 정보 수정")
    @PutMapping(value = "/api/member-info/workerCard", consumes = {"multipart/form-data"})
    @WorkerRoleRequired
    public ResponseEntity<Response> updateWorkerCardImage(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestPart(name = "request") WorkerCardRequest request,
        @RequestPart(name = "workerCardImage") MultipartFile workerCardImage) {
        memberInfoService.updateWorkerCardImage(principalDetails.getMember().getId(), request, workerCardImage);
        return ResponseEntity.ok(new Response("근로자 카드 정보 수정 완료"));
    }
}
