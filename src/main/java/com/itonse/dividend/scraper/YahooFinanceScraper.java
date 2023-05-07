package com.itonse.dividend.scraper;

import com.itonse.dividend.model.Company;
import com.itonse.dividend.model.Dividend;
import com.itonse.dividend.model.ScrapedResult;
import com.itonse.dividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component   // 빈으로 사용
public class YahooFinanceScraper implements Scraper{

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    private static final long START_TIME = 86400;  // 시작 날짜 (60 * 60 * 24 초)

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);
        try {
            long now = System.currentTimeMillis() / 1000;  //현재 시간을 초로 나타냄.

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);// 포맷팅
            // Jsoup 라이브러리를 이용하여 웹사이트의 데이터 스크래핑
            Connection connection = Jsoup.connect(url);   // Jsoup.connect 메소드를 사용하여 해당 URL 로 연결, Connection 객체 생성
            Document document = connection.get();   // URL 에서 HTML 문서를 가져와 Document 객체로 파싱

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");   // 데이터 테이블 가져오기. (Document 는 Elements 를 상속받음)
            Element tableEle = parsingDivs.get(0);    // 테이블 하나만 가져옴

            Element tbody = tableEle.children().get(1);   // 그 테이블의 tbody(본문) 부분을 가져옴
            List<Dividend> dividends = new ArrayList<>();    // Dividend 을 담을 리스트
            for (Element e: tbody.children()) {   // 본문 속성을 순회 (한 줄씩)
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {    // 여기서 텍스트 내용이  '~ Dividend' 으로 끝나는 배당금 데이터만 필요
                    continue;     // 아니면 건너뛴다
                }
                // 공백을 기준으로 쪼개서, 년월일과 배당금 순서로 추출해 변수에 대입
                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);   // Jan -> 1
                int day = Integer.valueOf(splits[1].replace(",", ""));   // day 에는 반점이 있어서 제거
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {   // -1
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(Dividend.builder()    // dividends 리스트에 담기
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());

//                System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
            }
            scrapResult.setDividends(dividends);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {   // ticker 를 입력하면 회사의 메타 정보를 반환
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[1].trim();

            return Company.builder()
                            .ticker(ticker)
                            .name(title)
                            .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
