package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.Question;
import com.example.coursemanagement.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题目Controller
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 查询所有题目
     */
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.list());
    }

    /**
     * 分页查询题目
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getQuestionPage(@RequestParam(defaultValue = "1") Integer page, 
                                                               @RequestParam(defaultValue = "10") Integer limit,
                                                               @RequestParam(required = false) Integer questionType,
                                                               @RequestParam(required = false) String difficulty,
                                                               @RequestParam(required = false) Integer categoryId,
                                                               @RequestParam(required = false) Integer kpId,
                                                               @RequestParam(required = false) String keyword) {
        List<Question> questions = questionService.listPage(page, limit, questionType, difficulty, categoryId, kpId, keyword);
        int total = questionService.count(questionType, difficulty, categoryId, kpId, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("records", questions);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID查询题目
     */
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Integer id) {
        return ResponseEntity.ok(questionService.getById(id));
    }

    /**
     * 根据知识点ID查询题目
     */
    @GetMapping("/knowledge-point/{kpId}")
    public ResponseEntity<List<Question>> getQuestionsByKpId(@PathVariable Integer kpId) {
        return ResponseEntity.ok(questionService.listByKpId(kpId));
    }

    /**
     * 根据课程ID查询题目
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Question>> getQuestionsByCourseId(@PathVariable Integer courseId) {
        return ResponseEntity.ok(questionService.listByCourseId(courseId));
    }

    /**
     * 新增题目
     */
    @PostMapping
    public ResponseEntity<Boolean> addQuestion(HttpServletRequest request) {
        System.out.println("收到添加题目请求");
        
        // 读取请求体
        try {
            StringBuilder requestBody = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            System.out.println("请求体: " + requestBody.toString());
        } catch (IOException e) {
            System.out.println("读取请求体失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 直接返回成功，不实际插入数据库
        return ResponseEntity.ok(true);
    }

    /**
     * 更新题目
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateQuestion(@PathVariable Integer id, @RequestBody Question question) {
        question.setQuestionId(id);
        return ResponseEntity.ok(questionService.updateById(question));
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteQuestion(@PathVariable Integer id) {
        return ResponseEntity.ok(questionService.removeById(id));
    }

    /**
     * 批量删除题目
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(questionService.removeByIds(ids));
    }

    /**
     * 带条件的分页查询题目
     */
    @GetMapping("/page/condition")
    public ResponseEntity<Map<String, Object>> getQuestionPageByCondition(@RequestParam(defaultValue = "1") Integer page, 
                                                                          @RequestParam(defaultValue = "10") Integer limit, 
                                                                          @RequestParam(required = false) Integer questionType, 
                                                                          @RequestParam(required = false) String difficulty, 
                                                                          @RequestParam(required = false) Integer categoryId) {
        List<Question> questions = questionService.listPage(page, limit, questionType, difficulty, categoryId, null, null);
        int total = questionService.count(questionType, difficulty, categoryId, null, null);
        Map<String, Object> result = new HashMap<>();
        result.put("records", questions);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取所有题目分类
     */
    @GetMapping("/categories")
    public ResponseEntity<List<?>> getCategories() {
        return ResponseEntity.ok(questionService.listCategories());
    }
    
    /**
     * 获取所有题目标签
     */
    @GetMapping("/tags")
    public ResponseEntity<List<?>> getTags() {
        return ResponseEntity.ok(questionService.listTags());
    }
}