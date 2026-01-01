package com.example.coursemanagement.test;

import com.example.coursemanagement.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class KnowledgePointTest implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(KnowledgePointTest.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 开始查询知识点数据 ===");
        
        // 查询前20个知识点，包含课程ID和课程名称
        String sql = "SELECT kp.kp_id, kp.kp_name, kp.course_id, c.course_name " +
                     "FROM knowledge_point kp " +
                     "JOIN course c ON kp.course_id = c.course_id " +
                     "LIMIT 20";
        
        jdbcTemplate.query(sql, (rs) -> {
            System.out.printf("知识点ID: %d, 名称: %s, 课程ID: %d, 课程名称: %s%n",
                             rs.getInt("kp_id"),
                             rs.getString("kp_name"),
                             rs.getInt("course_id"),
                             rs.getString("course_name"));
            return null;
        });
        
        System.out.println("=== 知识点数据查询结束 ===");
        
        // 退出程序
        System.exit(0);
    }
}