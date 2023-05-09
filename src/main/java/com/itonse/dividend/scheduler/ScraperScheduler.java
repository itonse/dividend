package com.itonse.dividend.scheduler;

import com.itonse.dividend.model.Company;
import com.itonse.dividend.model.ScrapedResult;
import com.itonse.dividend.model.constants.CacheKey;
import com.itonse.dividend.persist.CompanyRepository;
import com.itonse.dividend.persist.DividendRepository;
import com.itonse.dividend.persist.entity.CompanyEntity;
import com.itonse.dividend.persist.entity.DividendEntity;
import com.itonse.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j  // 로깅기능 사용
@Component
@EnableCaching
@AllArgsConstructor   // companyRepository 초기화
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    // 일정 주기마다 수행
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)   // 캐시에있는 데이터가 비워짐
    @Scheduled(cron = "${scheduler.scrap.yahoo}")   // 실행 주기는 yml 파일에서 설정
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company: companies) {
            log.info("Started scrapping -> " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
                    new Company(company.getTicker(), company.getName()));   // company 는 companyEntity 타입이라 맵핑 필요

            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()     //  scrapedResult 에는 List<Dividend> 가 들어있음.
                    // Dividend -> devidendEntity 맵핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입 (존재X 경우에만)
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());  // DB에서 존재 여부 확인
                        if (!exists) {
                            this.dividendRepository.save(e);   // 존재하지 않은 값 들만 저장
                            log.info("insert new dividend -> " + e.toString());
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000);   // 3초간 스레드 정지
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

    }
}
