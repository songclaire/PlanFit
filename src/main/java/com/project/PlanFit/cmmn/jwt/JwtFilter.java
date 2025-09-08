//⭐4. 요청에서 토큰 꺼내고 인증 객체 등록
// -> 서버는 이 요청이 "누가 보냈는지(userId)" 알고 있음, 컨트롤러에서 Authentication 파라미터로 유저 정보 꺼낼 수 있음
// -> 하지만 아직 Spring Security는 아직 이 필터의 존재를 모름

package com.project.PlanFit.cmmn.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        System.out.println("✅✅✅ JwtFilter 패스 확인: " + path);
        return path.equals("/api/login") || path.equals("/api/signup");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 헤더에서 토큰 꺼냄
        String token = resolveToken(request);

        // 2. 토큰이 존재하고, 유효하면
        if (token != null && jwtProvider.validateToken(token)) {

            // 3. 토큰에서 userId 추출
            String userId = jwtProvider.getUserId(token);

            // 4. 인증 객체 생성
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );

            // 5. SecurityContext에 등록 → 이 요청은 "로그인된 사용자"로 인식됨
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 6. 다음 필터로 넘어감
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // "Bearer " 제거
        }
        return null;
    }
}
