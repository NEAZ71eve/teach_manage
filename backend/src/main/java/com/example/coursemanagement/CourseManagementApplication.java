package com.example.coursemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 本科专业管理系统主应用类
 */
@SpringBootApplication
@EnableCaching
public class CourseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseManagementApplication.class, args);
        System.out.println("\n==================================");
        System.out.println("本科专业管理系统后端已启动");
        System.out.println("访问地址: http://localhost:8080/api");
        System.out.println("==================================\n");
    }

}
