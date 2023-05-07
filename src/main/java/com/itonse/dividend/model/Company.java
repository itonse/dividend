package com.itonse.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Company {    // model 클래스 (CompanyEntity 와 구분)

    private String ticker;
    private String name;
}
