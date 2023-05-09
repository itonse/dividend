package com.itonse.dividend.web;

import com.itonse.dividend.model.Auth;
import com.itonse.dividend.security.TokenProvider;
import com.itonse.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")    // 회원가입 API
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        var result = this.memberService.register(request);   // request 를 멤버 레파지토리에 저장
        return ResponseEntity.ok(result);   // 결과 반환
    }

    @PostMapping("/signin")    // 로그인 API
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        var member = this.memberService.authenticate(request);   // 로그인시 검증
        var token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());  // 토큰 값 생성

        return ResponseEntity.ok(token);   // 토큰 반환
    }

}
