package com.example.coursemanagement.entity;

import lombok.Data;

import java.util.List;

/**
 * 登录响应实体类
 */
@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private User user;
    private List<Role> roles;
    private List<Permission> permissions;
}