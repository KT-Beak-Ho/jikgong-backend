package jikgong.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.member.dtos.CompanySearchResponse;
import jikgong.domain.member.service.MemberCompanyService;
import jikgong.global.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberCompanyController {
    private final MemberCompanyService memberCompanyService;

    @Operation(summary = "기업 검색")
    @GetMapping("/api/member/search/company")
    public ResponseEntity<Response> searchCompany(@RequestParam(name = "keyword", required = false) String keyword) {
        List<CompanySearchResponse> companySearchResponseList = memberCompanyService.searchCompany(keyword);
        return ResponseEntity.ok(new Response(companySearchResponseList, "기업 검색 결과 반환 완료"));
    }
}
