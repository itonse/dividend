package com.itonse.dividend.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")   // 공통 경로
public class CompanyController {

    @GetMapping("/autocomplete")    // 배당금 검색할 때 자동완성 기능 API
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        return null;
    }

    @GetMapping  // 회사리스트 조회 API
    public ResponseEntity<?> searchCompany() {
        return null;
    }

    @PostMapping   // 배당금 데이터 저장 API
    public ResponseEntity<?> addCompany() {
        return null;
    }

    @DeleteMapping   // 저장했던 회사 삭제 API
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
