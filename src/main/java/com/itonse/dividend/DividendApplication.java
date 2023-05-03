package com.itonse.dividend;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

//@SpringBootApplication
public class DividendApplication {

    public static void main(String[] args) {
        //SpringApplication.run(DividendApplication.class, args);

        try {
            // Jsoup 라이브러리를 이용하여 웹사이트의 데이터 스크래핑
            Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1649030400&interval=1wk&filter=history&frequency=1wk&includeAdjustedClose=true");
                // Jsoup.connect 메소드를 사용하여 해당 URL로 연결, Connection 객체 생성
            Document document = connection.get();   // URL에서 HTML 문서를 가져와 Document 객체로 파싱

            Elements eles = document.getElementsByAttributeValue("data-test", "historical-prices");   // 데이터 테이블 가져오기. (Document 는 Elements 를 상속받음)
            Element ele = eles.get(0);    // 테이블 하나만 가져옴

            Element tbody = ele.children().get(1);   // 그 테이블의 tbody(본문) 부분을 가져옴
            for (Element e: tbody.children()) {   // 본문 속성을 순회 (한 줄씩)
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {    // 여기서 텍스트 내용이  '~ Dividend' 으로 끝나는 배당금 데이터만 필요
                    continue;     // 아니면 건너뛴다
                }
                // 공백을 기준으로 쪼개서, 년월일과 배당금 순서로 추출해 변수에 대입
                String[] splits = txt.split(" ");
                String month = splits[0];
                int day = Integer.valueOf(splits[1].replace(",", ""));   // day에는 반점이 있어서 제거
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
