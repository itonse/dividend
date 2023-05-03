package com.itonse.dividend.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")  // 공통 경로
public class FinanceController {

    @GetMapping("/dividend/{companyName}")   // 배당금 조회 API
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        return null;
    }
}
