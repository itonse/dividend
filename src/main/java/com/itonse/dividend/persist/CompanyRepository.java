package com.itonse.dividend.persist;

import com.itonse.dividend.persist.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {   // <엔티티, 엔티티 id 타입>
    boolean existsByTicker(String ticker);    // ticker 의 회사가 DB에 존재하는지 여부
}
