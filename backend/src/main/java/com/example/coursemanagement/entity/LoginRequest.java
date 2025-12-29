package com.example.coursemanagement.entity;

import lombok.Data;

/**
 * 登录请求实体类
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}