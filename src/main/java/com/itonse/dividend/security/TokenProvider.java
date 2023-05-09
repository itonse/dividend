package com.itonse.dividend.security;

import com.itonse.dividend.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;  // 1시간
    private static final String KEY_ROLES = "roles";

    private final MemberService memberService;

    @Value("{spring.jwt.secret}")
    private String secretKey;

    /**
     * 토큰 생성(발급)
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles) {   // 토큰 값 생성해서 반환
        Claims claims = Jwts.claims().setSubject(username);   // 사용자의 권한정보 저장
        claims.put(KEY_ROLES, roles);

        var now = new Date();   // 토큰 생성 시간
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);  // 토큰 만료 시간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, this.secretKey)   // 사용할 암호화 알고리즘, 비밀키
                .compact();  // 빌더 종료
    }

    public Authentication getAuthentication(String jwt) {    // jwt 토큰으로부터 인증정보 가져오기
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));
        // (사용자정보, 사용자권한정보를 갖는) 스프링에서 지원해주는 형태의 토큰으로 바꿔준다.
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();   // 넣어준 Subject 값(username) 반환
    }

    public boolean validateToken(String token) {   // 토큰이 유효한지 확인
        if (!StringUtils.hasText(token)) return false;

        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());  // 현재시간을 기준으로 토큰 만료시간이 남았는지
    }

    private Claims parseClaims(String token) {   // 토큰으로 부터 클레임 정보를 파싱
        try {
            return Jwts.parser().setSigningKey(this.secretKey).
                    parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {  // 토큰 시간이 경과 됨
            return e.getClaims();
        }
    }
}
