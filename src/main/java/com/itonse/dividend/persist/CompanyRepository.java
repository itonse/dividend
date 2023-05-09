package com.itonse.dividend.persist;

import com.itonse.dividend.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {   // <엔티티, 엔티티 id 타입>
    boolean existsByTicker(String ticker);    // ticker 의 회사가 DB에 존재하는지 여부

    Optional<CompanyEntity> findByName(String name);   // 회사명으로 회사의 정보를 찾음  (null 일 때를 방지하기 위해 Optional 타입으로 반환)

    Optional<CompanyEntity> findByTicker(String ticker);

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);    // LIKE 연산자 사용
}
