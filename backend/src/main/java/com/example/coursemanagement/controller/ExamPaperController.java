package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.ExamPaper;
import com.example.coursemanagement.entity.ExamPaperQuestion;
import com.example.coursemanagement.service.ExamPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 试卷Controller
 */
@RestController
@RequestMapping("/exam-paper")
public class ExamPaperController {

    @Autowired
    private ExamPaperService examPaperService;

    /**
     * 查询所有试卷
     */
    @GetMapping
    public ResponseEntity<List<ExamPaper>> getAllExamPapers() {
        return ResponseEntity.ok(examPaperService.list());
    }

    /**
     * 分页查询试卷
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getExamPaperPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        List<ExamPaper> papers = examPaperService.listPage(page, limit);
        int total = examPaperService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("records", papers);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID查询试卷
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamPaper> getExamPaperById(@PathVariable String id) {
        return ResponseEntity.ok(examPaperService.getById(id));
    }

    /**
     * 根据课程ID查询试卷
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ExamPaper>> getExamPapersByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(examPaperService.listByCourseId(courseId));
    }

    /**
     * 新增试卷
     */
    @PostMapping
    public ResponseEntity<Boolean> addExamPaper(@RequestBody ExamPaper paper) {
        return ResponseEntity.ok(examPaperService.save(paper));
    }

    /**
     * 更新试卷
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateExamPaper(@PathVariable String id, @RequestBody ExamPaper paper) {
        paper.setPaperId(Integer.parseInt(id));
        return ResponseEntity.ok(examPaperService.updateById(paper));
    }

    /**
     * 删除试卷
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteExamPaper(@PathVariable String id) {
        return ResponseEntity.ok(examPaperService.removeById(id));
    }

    /**
     * 批量删除试卷
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<String> ids) {
        return ResponseEntity.ok(examPaperService.removeByIds(ids));
    }

    /**
     * 获取试卷的题目列表
     */
    @GetMapping("/{id}/questions")
    public ResponseEntity<List<ExamPaperQuestion>> getPaperQuestions(@PathVariable String id) {
        return ResponseEntity.ok(examPaperService.getPaperQuestions(id));
    }

    /**
     * 添加题目到试卷
     */
    @PostMapping("/question")
    public ResponseEntity<Boolean> addQuestionToPaper(@RequestBody ExamPaperQuestion examPaperQuestion) {
        return ResponseEntity.ok(examPaperService.addQuestionToPaper(examPaperQuestion));
    }

    /**
     * 从试卷中删除题目
     */
    @DeleteMapping("/question/{id}")
    public ResponseEntity<Boolean> removeQuestionFromPaper(@PathVariable Integer id) {
        return ResponseEntity.ok(examPaperService.removeQuestionFromPaper(id));
    }

    /**
     * 清空试卷题目
     */
    @DeleteMapping("/{id}/questions")
    public ResponseEntity<Boolean> clearPaperQuestions(@PathVariable String id) {
        return ResponseEntity.ok(examPaperService.clearPaperQuestions(id));
    }

    /**
     * 自动组卷
     */
    @PostMapping("/auto-generate")
    @SuppressWarnings("unchecked")
    public ResponseEntity<String> autoGeneratePaper(@RequestBody Map<String, Object> params) {
        String courseId = String.valueOf(params.get("courseId"));
        Double totalScore = (Double) params.get("totalScore");
        Map<String, Double> knowledgePointWeights = (Map<String, Double>) params.get("knowledgePointWeights");
        Map<String, Double> difficultyDistribution = (Map<String, Double>) params.get("difficultyDistribution");
        
        String paperId = examPaperService.autoGeneratePaper(courseId, totalScore, knowledgePointWeights, difficultyDistribution);
        return ResponseEntity.ok(paperId);
    }
}
