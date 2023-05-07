package com.itonse.dividend.persist.entity;

import com.itonse.dividend.model.Dividend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "DIVIDEND")
@Getter
@ToString
@NoArgsConstructor
public class DividendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //  PK

    private Long companyId;   // 어떤 회사의 배당금 정보인지

    private LocalDateTime date;   // 배당금 지금일

    private String dividend;   // 배당금 금액

    public DividendEntity(Long companyId, Dividend dividend) {   // 모델 인스턴스를 -> 엔티티 인스턴스로 바꾸기 쉽게
        this.companyId = companyId;
        this.date = dividend.getDate();   // 배당금 지급일
        this.dividend = dividend.getDividend();   // 배당금액
    }
}
