package com.itonse.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {    // model 클래스 (CompanyEntity 와 구분)

    private String ticker;
    private String name;
}
