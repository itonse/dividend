package com.itonse.dividend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {   // 요청이 들어올 때 마다 아래 doFilterInternal 가 먼저 실행됨.

    public static final String TOKEN_HEADER = "Authorization";  // 토큰 헤더
    public static final String TOKEN_PREFIX = "Bearer ";  // 밸류

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)   // 요청이 들어올 때 마다 요청에 토큰이 포함되어있는지와, 그 토큰이 유효한지 확인
            throws ServletException, IOException {
        String token = this.resolveTokenFromRequest(request);

        if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {   // 토큰을 가지고 있고, 토큰이 유효하면
            // 토큰 유효성 검증
            Authentication auth = this.tokenProvider.getAuthentication(token);  // 토큰이 유효하다면
            SecurityContextHolder.getContext().setAuthentication(auth);  // 인증정보를 Context 에 담는다.

        }

        filterChain.doFilter(request, response);  // 유효하지 않다면, 바로 실행
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);  // 키에 해당하는 헤더의 밸류가 나옴

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());  // PREFIX 를 제외한 실제 토큰을 반환
        }

        return null;  // 없을 경우 null 반환
    }
}
