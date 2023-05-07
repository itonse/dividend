package com.itonse.dividend.web;

import com.itonse.dividend.model.Company;
import com.itonse.dividend.persist.entity.CompanyEntity;
import com.itonse.dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")   // 공통 경로
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/autocomplete")    // 배당금 검색할 때 자동완성 기능 API
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        return null;
    }

    @GetMapping  // 회사리스트 조회 API
    public ResponseEntity<?> searchCompany(final Pageable pageable) {    // 페이지 기능 추가
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    @PostMapping   // 회사 저장 API
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();   // 앞 뒤 공백 제거
        if (ObjectUtils.isEmpty(ticker)) {    // ticker 값을 빈 값으로 입력한 경우 에러 발생
            throw new RuntimeException("ticker is empty");
        }

        Company company = this.companyService.save(ticker);    // ticker 에 해당하는 회사를 저장한 후, 반환 된 회사정보를 저장
        return ResponseEntity.ok(company);   // ResponseEntity 에 회사정보 반환
    }

    @DeleteMapping    // 저장했던 회사 삭제 API
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
