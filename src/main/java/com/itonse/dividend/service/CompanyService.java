package com.itonse.dividend.service;

import com.itonse.dividend.model.Company;
import com.itonse.dividend.model.ScrapedResult;
import com.itonse.dividend.persist.CompanyRepository;
import com.itonse.dividend.persist.DividendRepository;
import com.itonse.dividend.persist.entity.CompanyEntity;
import com.itonse.dividend.persist.entity.DividendEntity;
import com.itonse.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);// ticker 의 회사가 DB에 존재하는지 여부
        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);  // 존재한다면 에러 발생
        }
        return this.storeCompanyAndDividend(ticker);    // 존재하지 않는다면 저장 후, 받은 결과값을 반환
    }


    private Company storeCompanyAndDividend(String ticker) {   // DB에 ticker에 해당하는 회사 정보를 저장한 후, 그 Company 인스턴스를 반환
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {    // company 정보가 있는지 확인
            throw new RuntimeException("failed to scrap ticker -> " + ticker);   // 정보가 없다면 에러 메시지 발생
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));// 레파지토리에는 company 가 아닌 companyEntity 타입이 저장 되어야 함.
        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()   // getDividends 는 Dividend 의 리스트
                                                .map(e -> new DividendEntity(companyEntity.getId(), e))    // e: 위 리스트의 Dividend 객체들.  Dividend -> DividendEntity 맵핑
                                                .collect(Collectors.toList());//  결과값을 지정한 리스트 타입으로 반환
        this.dividendRepository.saveAll(dividendEntityList);   // 레파지토리에 모두 저장
        return company;   // 저장한 회사 정보를 반환
    }
}
