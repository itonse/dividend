package com.itonse.dividend.service;

import com.itonse.dividend.model.Company;
import com.itonse.dividend.model.ScrapedResult;
import com.itonse.dividend.persist.CompanyRepository;
import com.itonse.dividend.persist.DividendRepository;
import com.itonse.dividend.persist.entity.CompanyEntity;
import com.itonse.dividend.persist.entity.DividendEntity;
import com.itonse.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {    // SpringBoot의 빈 -> 싱글톤

    private final Trie trie;   // 트라이
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

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {     // 저장한 전체 회사 조회
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {   // DB에 ticker 에 해당하는 회사 정보를 저장한 후, 그 Company 인스턴스를 반환
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {    // company 정보가 있는지 확인
            throw new RuntimeException("failed to scrap ticker -> " + ticker);   // 정보가 없다면 에러 메시지 발생
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));// 레파지토리에는 company 가 아닌 companyEntity 타입이 저장 되어야 함.
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()   // getDividends 는 Dividend 의 리스트
                                                .map(e -> new DividendEntity(companyEntity.getId(), e))    // e: 위 리스트의 Dividend 객체들.  Dividend -> DividendEntity 맵핑
                                                .collect(Collectors.toList());//  결과값을 지정한 리스트 타입으로 반환
        this.dividendRepository.saveAll(dividendEntities);   // 레파지토리에 모두 저장
        return company;   // 저장한 회사 정보를 반환
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {    // LIKE 연산
        Pageable limit = PageRequest.of(0, 10);    // 한 번에 10개씩만 가져오기
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()   // company 엔티티에 있는 회사명들 추출
                                .map(e -> e.getName())
                                .collect(Collectors.toList());

    }

    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);    // 트라이에 회사명 저장
    }

    public List<String> autocomplete(String keyword) {   // 자동완성 기능 (트라이에서 단어를 찾아오는 로직)
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)    // 최대 10개  가져오기
                .collect(Collectors.toList());
    }

    // 트라이에 저장된 키워드 삭제
    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }
}
