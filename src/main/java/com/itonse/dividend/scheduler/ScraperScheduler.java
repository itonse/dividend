package com.itonse.dividend.scheduler;

import com.itonse.dividend.model.Company;
import com.itonse.dividend.model.ScrapedResult;
import com.itonse.dividend.persist.CompanyRepository;
import com.itonse.dividend.persist.DividendRepository;
import com.itonse.dividend.persist.entity.CompanyEntity;
import com.itonse.dividend.persist.entity.DividendEntity;
import com.itonse.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j  // 로깅기능 사용
@Component
@AllArgsConstructor   // companyRepository 초기화
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    @Scheduled(fixedDelay = 1000)
    public void test1() throws  InterruptedException {
        Thread.sleep(10000);
        System.out.println(Thread.currentThread().getName() + " -> 테스트 1 : " + LocalDateTime.now());
    }

    @Scheduled(fixedDelay = 1000)
    public void test2() {
        System.out.println(Thread.currentThread().getName() + " -> 테스트 2 : " + LocalDateTime.now());
    }

    // 일정 주기마다 수행
//    @Scheduled(cron = "${scheduler.scrap.yahoo}")   // 실행 주기는 yml 파일에서 설정
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company: companies) {
            log.info("Started scrapping -> " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()     // company 는 companyEntity 타입이라 맵핑 필요
                                                                                .name(company.getName())
                                                                                .ticker(company.getTicker())
                                                                                .build());
        // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()     //  scrapedResult 에는 List<Dividend> 가 들어있음.
                    // Dividend -> devidendEntity 맵핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입 (존재X 경우에만)
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());  // DB에서 존재 여부 확인
                        if (!exists) {
                            this.dividendRepository.save(e);   // 존재하지 않은 값 들만 저장
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
