package com.itonse.dividend.service;

import com.itonse.dividend.model.Company;
import com.itonse.dividend.model.Dividend;
import com.itonse.dividend.model.ScrapedResult;
import com.itonse.dividend.model.constants.CacheKey;
import com.itonse.dividend.persist.CompanyRepository;
import com.itonse.dividend.persist.DividendRepository;
import com.itonse.dividend.persist.entity.CompanyEntity;
import com.itonse.dividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)  // 캐시 사용
    // 회사명으로 배당금 조회하기 (이 메서드는 캐시에 데이터가 없을 때만 실행이 되고, 나오는 리턴 값은 캐시에 추가한다. 이후에는 이 메서드 실행 없이 바로 캐시에서 데이터를 가져오게 된다.)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);   // DB 에서 조회 할 때만 로그가 찍히고, 캐시에서 조회할 때는 로그가 찍히지 않는다.
        // 1. 회사명을 기준으로 회사 정보를 조회 by companyRepository
        CompanyEntity company = this.companyRepository.findByName(companyName)
                                                 .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));  // 값이 없으면 런타임 에러 발생

        // 2. 조회된 회사 ID로 배당금을 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()   // dividendEntity List -> dividend List 맵핑
                                                    .map(e -> new Dividend(e.getDate(),e.getDividend()))
                                                    .collect(Collectors.toList());


        return new ScrapedResult(new Company(company.getTicker(), company.getName()),     // companyEntity -> company 맵핑
                                        dividends);   // 위에서 맵핑된 dividend List
    }
}
