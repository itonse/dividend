package com.itonse.dividend.scraper;

import com.itonse.dividend.model.Company;
import com.itonse.dividend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
