package com.itonse.dividend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Company {    // model 클래스 (CompanyEntity 와 구분)

    private String ticker;
    private String name;
}
