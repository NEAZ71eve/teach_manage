package com.example.coursemanagement.entity;

import lombok.Data;

/**
 * 权限实体类
 */
@Data
public class Permission {

    private Integer permissionId;
    private String permissionName;
    private String permissionCode;
    private String description;
    private String url;
    private String method;
}
