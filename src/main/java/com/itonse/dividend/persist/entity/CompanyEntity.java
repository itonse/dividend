package com.itonse.dividend.persist.entity;

import com.itonse.dividend.model.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "COMPANY")
@Getter
@ToString
@NoArgsConstructor
public class CompanyEntity {    // DB와 직접적으로 매핑되는 클래스

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ID가 생성되는 방식: Auto Increment
    private Long id;  // PK

    @Column(unique = true)    // 중복 허용X
    private String ticker;

    private String name;   // 회사명

    public CompanyEntity(Company company) {    // company 인스턴스를 만들어 주는 생성자
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
