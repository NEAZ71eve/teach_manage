package com.example.coursemanagement.entity;

import lombok.Data;

/**
 * 用户角色关联实体类
 */
@Data
public class UserRole {

    private Integer id;
    private Integer userId;
    private Integer roleId;
}
