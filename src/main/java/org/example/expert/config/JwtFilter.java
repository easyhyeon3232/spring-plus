package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * /auth 요청은 통과
 * JWT를 꺼내서 검증
 * request에 userId, email, userRole 저장
 * /admin이면 ADMIN인지 직접 검사
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        Filter.super.init(filterConfig);
//    }

    // singup/singin 요청은 JwtFilter가 실행되지 않는다.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/auth");
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest httpRequest,
            @NonNull HttpServletResponse httpResponse,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String bearerJwt = httpRequest.getHeader("Authorization");

        if (bearerJwt == null) {
            // 토큰 없으면 인증 없이 넘기고, SecurityConfig가 접근 가능 여부 판단
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        try {
            // Authorization 헤더에서 "Bearer"를 제거하고 순수 JWT 문자열만 꺼낸다.
            String jwt = jwtUtil.substringToken(bearerJwt);

            // JWT의 서명과 만료 여부를 검증하고, payload에 들어있는 사용자 정보를 꺼낸다.
            Claims claims = jwtUtil.extractClaims(jwt);

            if(claims == null) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            // 토큰 payload에서 사용자 정보를 꺼낸단.
            Long userId = Long.parseLong(claims.getSubject());
            String email = claims.get("email", String.class);
            UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

            // Controller에서 @AuthenticationPrincipal로 받을 사용자 정보
            AuthUser authUser = new AuthUser(userId, email, userRole);

            // hasRole("ADMIN")은 내부적으로 ROLE_ADMIN 권한을 확인한다. 그래서 ROLE_prefix를 붙여준다.
            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + userRole.name())
            );

            // Spring Security가 현재 요청을 인증된 요청으로 인식할 수 있게 Authentication을 만든다.
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(authUser, null, authorities);

            // 현재 요청의 인증 정보를 SecurityContext에 저장
            // 이후 @AuthenticationPrincipal AuthUser authUser로 꺼낼 수 있음
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(httpRequest, httpResponse);

        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("Internal server error", e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

//    @Override
//    public void destroy() {
//        Filter.super.destroy();
//    }
}
