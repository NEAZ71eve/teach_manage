package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.Question;
import com.example.coursemanagement.entity.QuestionCategory;
import com.example.coursemanagement.entity.QuestionTag;
import com.example.coursemanagement.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAuthority('question:list')")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.list());
    }

    /**
     * 分页查询题目
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('question:list')")
    public ResponseEntity<Map<String, Object>> getQuestionPage(@RequestParam(defaultValue = "1") Integer page, 
                                                               @RequestParam(defaultValue = "10") Integer limit,
                                                               @RequestParam(required = false) Integer questionType,
                                                               @RequestParam(required = false) String difficulty,
                                                               @RequestParam(required = false) Integer categoryId) {
        List<Question> questions = questionService.listPage(page, limit, questionType, difficulty, categoryId);
        int total = questionService.count(questionType, difficulty, categoryId);
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
    @PreAuthorize("hasAuthority('question:list')")
    public ResponseEntity<Question> getQuestionById(@PathVariable Integer id) {
        return ResponseEntity.ok(questionService.getById(id));
    }

    /**
     * 根据课程ID查询题目
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('question:list')")
    public ResponseEntity<List<Question>> getQuestionsByCourseId(@PathVariable Integer courseId) {
        return ResponseEntity.ok(questionService.listByCourseId(courseId));
    }

    /**
     * 根据知识点ID查询题目
     */
    @GetMapping("/knowledge-point/{kpId}")
    @PreAuthorize("hasAuthority('question:list')")
    public ResponseEntity<List<Question>> getQuestionsByKpId(@PathVariable Integer kpId) {
        return ResponseEntity.ok(questionService.listByKpId(kpId));
    }

    /**
     * 根据题型查询题目
     */
    @GetMapping("/type/{questionType}")
    @PreAuthorize("hasAuthority('question:list')")
    public ResponseEntity<List<Question>> getQuestionsByType(@PathVariable Integer questionType) {
        return ResponseEntity.ok(questionService.listByType(questionType));
    }

    /**
     * 根据难度查询题目
     */
    @GetMapping("/difficulty/{difficulty}")
    @PreAuthorize("hasAuthority('question:list')")
    public ResponseEntity<List<Question>> getQuestionsByDifficulty(@PathVariable String difficulty) {
        return ResponseEntity.ok(questionService.listByDifficulty(difficulty));
    }
    
    /**
     * 根据分类查询题目
     */
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAuthority('question:list')")
    public ResponseEntity<List<Question>> getQuestionsByCategoryId(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(questionService.listByCategoryId(categoryId));
    }

    /**
     * 新增题目
     */
    @PostMapping
    // @PreAuthorize("hasAuthority('question:add')")
    public ResponseEntity<Boolean> addQuestion(@RequestBody Question question) {
        System.out.println("=== 接收到新增题目请求 ===");
        System.out.println("请求体: " + question);
        try {
            boolean result = questionService.save(question);
            System.out.println("新增题目结果: " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("新增题目失败，错误信息: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(false);
        }
    }

    /**
     * 更新题目
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('question:edit')")
    public ResponseEntity<Boolean> updateQuestion(@PathVariable Integer id, @RequestBody Question question) {
        question.setQuestionId(id);
        return ResponseEntity.ok(questionService.updateById(question));
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('question:delete')")
    public ResponseEntity<Boolean> deleteQuestion(@PathVariable Integer id) {
        return ResponseEntity.ok(questionService.removeById(id));
    }

    /**
     * 批量删除题目
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('question:delete')")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(questionService.removeByIds(ids));
    }
    
    /**
     * 查询所有题目分类
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('question:list')")
    public ResponseEntity<List<QuestionCategory>> getCategories() {
        return ResponseEntity.ok(questionService.listCategories());
    }
    
    /**
     * 查询所有题目标签
     */
    @GetMapping("/tags")
    @PreAuthorize("hasAuthority('question:list')")
    public ResponseEntity<List<QuestionTag>> getTags() {
        return ResponseEntity.ok(questionService.listTags());
    }
    
    /**
     * 更新题目使用状态
     */
    @PutMapping("/{id}/used-status")
    @PreAuthorize("hasAuthority('question:edit')")
    public ResponseEntity<Boolean> updateUsedStatus(@PathVariable Integer id, @RequestParam Integer isUsed) {
        return ResponseEntity.ok(questionService.updateUsedStatus(id, isUsed));
    }
    
    /**
     * 审核题目
     */
    @PutMapping("/{id}/review")
    @PreAuthorize("hasAuthority('question:edit')")
    public ResponseEntity<Boolean> reviewQuestion(@PathVariable Integer id, @RequestParam String status, @RequestParam Integer reviewerId, @RequestParam(required = false) String remark) {
        return ResponseEntity.ok(questionService.reviewQuestion(id, status, reviewerId, remark));
    }
}