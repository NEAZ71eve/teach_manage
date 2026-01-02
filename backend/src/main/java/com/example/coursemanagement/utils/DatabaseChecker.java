package com.example.coursemanagement.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库检查工具类，用于打印数据库表结构
 */
@Component
public class DatabaseChecker {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 检查数据库结构
     */
    public void checkDatabase() {
        System.out.println("=== 数据库结构检查 ===");
        
        // 检查数据库连接
        System.out.println("检查数据库连接...");
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            System.out.println("数据库连接成功: " + (count != null ? "连接正常" : "连接异常"));
        } catch (Exception e) {
            System.out.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // 检查course表是否存在
        System.out.println("\n检查course表是否存在...");
        try {
            Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'course'",
                Integer.class
            );
            System.out.println("course表存在: " + (tableCount != null && tableCount > 0 ? "是" : "否"));
        } catch (Exception e) {
            System.out.println("检查course表失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 检查course表的结构
        System.out.println("\ncourse表的结构:");
        try {
            jdbcTemplate.query("DESCRIBE course", rs -> {
                System.out.printf("%-20s %-20s %-5s %-5s %-15s %-10s\n", 
                    "Field", "Type", "Null", "Key", "Default", "Extra");
                System.out.println("--------------------------------------------------------------------------------------------------");
                while (rs.next()) {
                    String fieldName = rs.getString("Field");
                    String type = rs.getString("Type");
                    String nullAble = rs.getString("Null");
                    String key = rs.getString("Key");
                    String defaultValue = rs.getString("Default");
                    String extra = rs.getString("Extra");
                    System.out.printf("%-20s %-20s %-5s %-5s %-15s %-10s\n", 
                        fieldName, type, nullAble, key, defaultValue, extra);
                }
            });
        } catch (Exception e) {
            System.out.println("获取course表结构失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== 数据库结构检查完成 ===");
    }
}