package com.itonse.dividend.service;

import com.itonse.dividend.model.Company;
import com.itonse.dividend.model.Dividend;
import com.itonse.dividend.model.ScrapedResult;
import com.itonse.dividend.persist.CompanyRepository;
import com.itonse.dividend.persist.DividendRepository;
import com.itonse.dividend.persist.entity.CompanyEntity;
import com.itonse.dividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {   // 회사명으로 배당금 조회하기

        // 1. 회사명을 기준으로 회사 정보를 조회 by companyRepository
        CompanyEntity company = this.companyRepository.findByName(companyName)
                                                 .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));  // 값이 없으면 런타임 에러 발생

        // 2. 조회된 회사 ID로 배당금을 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = new ArrayList<>();   // dividendEntity List -> dividend List 맵핑
        for (var entity: dividendEntities) {
            dividends.add(Dividend.builder()
                    .date(entity.getDate())
                    .dividend(entity.getDividend())
                    .build());
        }

        return new ScrapedResult(Company.builder()     // companyEntity -> company 맵핑
                                        .ticker(company.getTicker())
                                        .name(company.getName())
                                        .build(),
                dividends);   // 위에서 맵핑된 dividend List
    }
}
