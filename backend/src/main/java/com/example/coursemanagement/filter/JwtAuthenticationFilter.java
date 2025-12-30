package com.example.coursemanagement.filter;

import com.example.coursemanagement.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取Authorization头
        String authorizationHeader = request.getHeader("Authorization");
        
        // 检查Authorization头是否存在且以Bearer开头
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 提取令牌
            String token = authorizationHeader.substring(7);
            
            // 验证令牌
            if (jwtUtils.validateToken(token)) {
                // 令牌有效，提取用户ID和用户名
                Integer userId = jwtUtils.getUserIdFromToken(token);
                String username = jwtUtils.getUsernameFromToken(token);
                
                // 将用户信息设置到请求属性中
                request.setAttribute("userId", userId);
                request.setAttribute("username", username);
            }
        }
        
        // 继续过滤链
        filterChain.doFilter(request, response);
    }
}