package com.itonse.dividend.service;

import com.itonse.dividend.exception.impl.AlreadyExistUserException;
import com.itonse.dividend.model.Auth;
import com.itonse.dividend.persist.entity.MemberEntity;
import com.itonse.dividend.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    public MemberEntity register(Auth.SignUp member) {  // 회원가입 기능
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if (exists) {
            throw new AlreadyExistUserException();
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));  // 민감 정보인 패스워드는 인코딩된 값으로 저장
        var result = this.memberRepository.save(member.toEntity());// 회원정보 저장

        return result;
    }

    public MemberEntity authenticate(Auth.SignIn member) {  // 로그인시 검증 하는 메소드

        var user = this.memberRepository.findByUsername(member.getUsername())   // user: 인코딩된 패스워드 값이 들어옴
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));

        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {    // 인코딩된 패스워드 값 비교
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        return user;
    }
}
