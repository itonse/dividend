package com.itonse.dividend.persist.entity;

import com.itonse.dividend.model.Dividend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "DIVIDEND")
@Getter
@ToString
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = { "companyId", "date" }   // companyId 와 date 컬럼을 기준으로 하는 '복합 유니크'키 설정 (스케쥴러시 중복 데이터 저장 방지)
                )
        }
)
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
