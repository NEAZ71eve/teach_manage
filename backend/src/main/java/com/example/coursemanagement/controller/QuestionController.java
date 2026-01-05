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
                                                               @RequestParam(required = false) Integer kpId,
                                                               @RequestParam(required = false) Integer courseId,
                                                               @RequestParam(required = false) String keyword) {
        List<Question> questions = questionService.listPage(page, limit, questionType, difficulty, kpId, courseId, keyword);
        int total = questionService.count(questionType, difficulty, kpId, courseId, keyword);
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
    public ResponseEntity<Boolean> addQuestion(@RequestBody Question question) {
        System.out.println("收到添加题目请求，题目内容: " + question.getQuestionContent());
        System.out.println("题目类型: " + question.getQuestionType());
        System.out.println("知识点ID: " + question.getKpId());
        System.out.println("难度: " + question.getDifficulty());
        System.out.println("分值: " + question.getScore());
        System.out.println("正确答案: " + question.getCorrectAnswer());
        
        // 调用服务层保存题目
        boolean result = questionService.save(question);
        System.out.println("保存结果: " + result);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新题目
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateQuestion(@PathVariable Integer id, @RequestBody Question question) {
        System.out.println("=== 开始更新题目 ===");
        System.out.println("更新的题目ID: " + id);
        System.out.println("更新的题目数据: " + question);
        
        question.setQuestionId(id);
        boolean result = questionService.updateById(question);
        
        System.out.println("更新结果: " + result);
        System.out.println("=== 更新题目结束 ===");
        
        return ResponseEntity.ok(result);
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
                                                                          @RequestParam(required = false) Integer kpId,
                                                                          @RequestParam(required = false) Integer courseId) {
        List<Question> questions = questionService.listPage(page, limit, questionType, difficulty, kpId, courseId, null);
        int total = questionService.count(questionType, difficulty, kpId, courseId, null);
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
