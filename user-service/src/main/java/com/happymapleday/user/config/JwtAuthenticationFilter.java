package com.happymapleday.user.config;

import com.happymapleday.user.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    
    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final Long userId;
        
        // Authorization 헤더가 없거나 Bearer로 시작하지 않으면 다음 필터로
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // "Bearer " 이후의 토큰 추출
        jwt = authHeader.substring(7);
        
        try {
            // JWT에서 사용자 ID 추출 및 토큰 타입 확인
            if (jwtService.isTokenValid(jwt) && jwtService.isAccessToken(jwt)) {
                userId = jwtService.getUserIdFromToken(jwt);
                
                // 이미 인증된 사용자가 아니고, 토큰이 유효하면 인증 설정
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Spring Security에 인증 정보 설정
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // 토큰이 유효하지 않은 경우 그냥 넘어감
        }
        
        filterChain.doFilter(request, response);
    }
} 