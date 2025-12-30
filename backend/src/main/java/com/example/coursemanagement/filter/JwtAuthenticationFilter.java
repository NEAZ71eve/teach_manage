package com.example.coursemanagement.filter;

import com.example.coursemanagement.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                // 令牌有效，提取用户ID、用户名、角色和权限
                Integer userId = jwtUtils.getUserIdFromToken(token);
                String username = jwtUtils.getUsernameFromToken(token);
                List<String> roles = jwtUtils.getRolesFromToken(token);
                List<String> permissions = jwtUtils.getPermissionsFromToken(token);
                
                // 将用户信息设置到请求属性中
                request.setAttribute("userId", userId);
                request.setAttribute("username", username);
                
                // 创建权限列表
                List<GrantedAuthority> authorities = new ArrayList<>();
                
                // 添加角色权限，前缀为ROLE_，符合Spring Security的要求
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
                
                // 添加功能权限
                for (String permission : permissions) {
                    authorities.add(new SimpleGrantedAuthority(permission));
                }
                
                // 创建认证对象
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                
                // 将认证对象设置到SecurityContext中
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        // 继续过滤链
        filterChain.doFilter(request, response);
    }
}