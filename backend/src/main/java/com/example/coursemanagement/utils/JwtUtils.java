package com.example.coursemanagement.utils;

import org.springframework.stereotype.Component;

/**
 * JWT工具类
 */
@Component
public class JwtUtils {

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public String generateToken(Integer userId, String username) {
        // 简化实现，直接返回空字符串
        return "";
    }

    /**
     * 从令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public Integer getUserIdFromToken(String token) {
        // 简化实现，直接返回1
        return 1;
    }

    /**
     * 从令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        // 简化实现，直接返回admin
        return "admin";
    }

    /**
     * 验证令牌是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        // 简化实现，直接返回false
        return false;
    }
}