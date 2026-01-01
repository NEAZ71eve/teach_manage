package com.example.coursemanagement;

import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.entity.KnowledgePoint;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.repository.KnowledgePointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestDatabaseCourseKnowledgePoints {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;

    @Test
    public void testDatabaseCourseKnowledgePoints() {
        // 查询所有课程
        List<Course> courses = courseRepository.findAll();
        System.out.println("=== 所有课程 ===");
        for (Course course : courses) {
            System.out.println("课程ID: " + course.getCourseId() + ", 课程名称: " + course.getCourseName());
        }

        // 查找数据库课程
        System.out.println("\n=== 查找数据库课程 ===");
        Course databaseCourse = null;
        for (Course course : courses) {
            if (course.getCourseName().contains("数据库")) {
                databaseCourse = course;
                break;
            }
        }

        if (databaseCourse != null) {
            System.out.println("找到数据库课程: " + databaseCourse.getCourseName() + ", 课程ID: " + databaseCourse.getCourseId());

            // 查询该课程的知识点
            System.out.println("\n=== 数据库课程的知识点 ===");
            List<KnowledgePoint> knowledgePoints = knowledgePointRepository.findByCourseId(databaseCourse.getCourseId());
            if (knowledgePoints.isEmpty()) {
                System.out.println("该课程没有知识点");
            } else {
                for (KnowledgePoint kp : knowledgePoints) {
                    System.out.println("知识点ID: " + kp.getKpId() + ", 知识点名称: " + kp.getKpName() + ", 课程ID: " + kp.getCourseId());
                }
            }
        } else {
            System.out.println("未找到数据库课程");
        }
    }
}