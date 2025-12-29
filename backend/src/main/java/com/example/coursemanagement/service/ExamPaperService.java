package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.ExamPaper;
import com.example.coursemanagement.entity.ExamPaperQuestion;

import java.util.List;
import java.util.Map;

/**
 * 试卷Service接口
 */
public interface ExamPaperService {

    /**
     * 查询所有试卷
     */
    List<ExamPaper> list();

    /**
     * 根据ID查询试卷
     */
    ExamPaper getById(String id);

    /**
     * 根据课程ID查询试卷
     */
    List<ExamPaper> listByCourseId(String courseId);

    /**
     * 新增试卷
     */
    boolean save(ExamPaper paper);

    /**
     * 更新试卷
     */
    boolean updateById(ExamPaper paper);

    /**
     * 删除试卷
     */
    boolean removeById(String id);

    /**
     * 批量删除试卷
     */
    boolean removeByIds(List<String> ids);

    /**
     * 分页查询试卷
     */
    List<ExamPaper> listPage(Integer page, Integer limit);

    /**
     * 查询试卷总数
     */
    int count();

    /**
     * 获取试卷的题目列表
     */
    List<ExamPaperQuestion> getPaperQuestions(String paperId);

    /**
     * 添加题目到试卷
     */
    boolean addQuestionToPaper(ExamPaperQuestion examPaperQuestion);

    /**
     * 从试卷中删除题目
     */
    boolean removeQuestionFromPaper(Integer id);

    /**
     * 清空试卷题目
     */
    boolean clearPaperQuestions(String paperId);

    /**
     * 自动组卷
     * @param courseId 课程ID
     * @param paperName 试卷名称
     * @param totalScore 试卷总分
     * @param knowledgePointWeights 知识点权重映射
     * @param difficultyDistribution 难度分布
     * @return 生成的试卷ID
     */
    String autoGeneratePaper(String courseId, String paperName, Double totalScore, Map<String, Double> knowledgePointWeights, Map<String, Double> difficultyDistribution);
}
