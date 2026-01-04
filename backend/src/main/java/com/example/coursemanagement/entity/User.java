package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {

    private Integer userId;
    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private Integer status;
    private Integer programId;
    private Integer roleId;
    private String roleName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
