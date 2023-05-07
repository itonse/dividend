package com.itonse.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor   // 모든 필드를 초기화하는 생성자 코드 사용 가능
public class ScrapedResult {

    private Company company;

    private List<Dividend> dividends;     // 배당금 리스트: DividendEntity로 매핑을 하면서 해당하는 companyId 값을 넣어주면서 저장.

    public ScrapedResult() { this.dividends = new ArrayList<>(); }
}
