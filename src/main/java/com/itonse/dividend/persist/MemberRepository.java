package com.itonse.dividend.persist;

import com.itonse.dividend.persist.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {   // 회원 정보를 DB에 넣고, 가져오는 레파지토리

    Optional<MemberEntity> findByUsername(String username);   // id 를 기준으로 회원 정보를 찾음

    boolean existsByUsername(String username);   // 회원가입 때 이미 존재하는 유저네임인지 확인하는 기능
}
