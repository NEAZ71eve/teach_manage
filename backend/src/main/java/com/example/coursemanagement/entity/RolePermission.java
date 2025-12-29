package com.example.coursemanagement.entity;

import lombok.Data;

/**
 * 角色权限关联实体类
 */
@Data
public class RolePermission {

    private Integer id;
    private Integer roleId;
    private Integer permissionId;
}
