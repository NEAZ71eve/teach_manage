package com.example.coursemanagement.entity;

import lombok.Data;

/**
 * 角色专业关联实体类
 */
@Data
public class RoleProgram {

    private Integer id;
    private Integer roleId;
    private Integer programId;
}