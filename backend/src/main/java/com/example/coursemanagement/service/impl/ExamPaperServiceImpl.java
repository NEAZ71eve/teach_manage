package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.ExamPaper;
import com.example.coursemanagement.entity.ExamPaperQuestion;
import com.example.coursemanagement.entity.Question;
import com.example.coursemanagement.repository.ExamPaperQuestionRepository;
import com.example.coursemanagement.repository.ExamPaperRepository;
import com.example.coursemanagement.repository.QuestionRepository;
import com.example.coursemanagement.service.ExamPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 试卷Service实现类
 */
@Service
public class ExamPaperServiceImpl implements ExamPaperService {

    @Autowired
    private ExamPaperRepository examPaperRepository;

    @Autowired
    private ExamPaperQuestionRepository examPaperQuestionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public List<ExamPaper> list() {
        return examPaperRepository.findAll();
    }

    @Override
    public ExamPaper getById(String id) {
        return examPaperRepository.findById(Integer.parseInt(id)).orElse(null);
    }

    @Override
    public List<ExamPaper> listByCourseId(String courseId) {
        return examPaperRepository.findByCourseId(Integer.parseInt(courseId));
    }

    @Override
    public boolean save(ExamPaper paper) {
        return examPaperRepository.save(paper) > 0;
    }

    @Override
    public boolean updateById(ExamPaper paper) {
        return examPaperRepository.update(paper) > 0;
    }

    @Override
    public boolean removeById(String id) {
        // 先删除试卷题目关联
        examPaperQuestionRepository.deleteByPaperId(id);
        return examPaperRepository.deleteById(Integer.parseInt(id)) > 0;
    }

    @Override
    public boolean removeByIds(List<String> ids) {
        // 先删除所有试卷的题目关联
        for (String id : ids) {
            examPaperQuestionRepository.deleteByPaperId(id);
        }
        // 转换为Integer列表
        List<Integer> integerIds = ids.stream().map(Integer::parseInt).collect(Collectors.toList());
        return examPaperRepository.deleteBatch(integerIds) > 0;
    }

    @Override
    public List<ExamPaper> listPage(Integer page, Integer limit) {
        return examPaperRepository.findByPage(page, limit);
    }

    @Override
    public int count() {
        return examPaperRepository.count();
    }

    @Override
    public List<ExamPaperQuestion> getPaperQuestions(String paperId) {
        return examPaperQuestionRepository.findByPaperId(paperId);
    }

    @Override
    public boolean addQuestionToPaper(ExamPaperQuestion examPaperQuestion) {
        return examPaperQuestionRepository.save(examPaperQuestion) > 0;
    }

    @Override
    public boolean removeQuestionFromPaper(Integer id) {
        return examPaperQuestionRepository.deleteById(id) > 0;
    }

    @Override
    public boolean clearPaperQuestions(String paperId) {
        return examPaperQuestionRepository.deleteByPaperId(paperId) > 0;
    }

    @Override
    @Transactional
    public String autoGeneratePaper(String courseId, String paperName, Double totalScore, Map<String, Double> knowledgePointWeights, Map<String, Double> difficultyDistribution) {
        System.out.println("开始自动组卷，参数: courseId=" + courseId + ", paperName=" + paperName + ", totalScore=" + totalScore);
        System.out.println("知识点权重: " + knowledgePointWeights);
        System.out.println("难度分布: " + difficultyDistribution);
        
        // 1. 计算知识点权重总和
        double weightSum = 0.0;
        for (Double value : knowledgePointWeights.values()) {
            weightSum += value != null ? value.doubleValue() : 0.0;
        }
        System.out.println("原始知识点权重总和: " + weightSum);
        
        // 2. 权重归一化处理：如果总和不等于1，自动调整为1
        if (Math.abs(weightSum - 1.0) > 0.02) {
            System.out.println("权重归一化处理前: " + knowledgePointWeights);
            // 创建新的权重映射，用于存储归一化后的权重
            Map<String, Double> normalizedWeights = new java.util.HashMap<>();
            for (Map.Entry<String, Double> entry : knowledgePointWeights.entrySet()) {
                Double originalWeight = entry.getValue() != null ? entry.getValue() : 0.0;
                // 归一化计算
                Double normalizedWeight = weightSum > 0 ? originalWeight / weightSum : 0.0;
                normalizedWeights.put(entry.getKey(), normalizedWeight);
            }
            // 替换原权重映射
            knowledgePointWeights = normalizedWeights;
            
            // 重新计算归一化后的总和
            weightSum = 0.0;
            for (Double value : knowledgePointWeights.values()) {
                weightSum += value != null ? value.doubleValue() : 0.0;
            }
            System.out.println("权重归一化处理后，总和: " + weightSum);
            System.out.println("归一化后的权重: " + knowledgePointWeights);
        }

        // 2. 创建试卷
        ExamPaper paper = new ExamPaper();
        paper.setPaperName(paperName != null && !paperName.isEmpty() ? paperName : "自动组卷 - " + courseId);
        paper.setCourseId(Integer.parseInt(courseId));
        paper.setTotalScore(totalScore);
        paper.setPaperType("自动组卷");
        paper.setCreateTeacher("admin"); // 默认创建教师
        examPaperRepository.save(paper);
        System.out.println("创建试卷成功，试卷ID: " + paper.getPaperId());

        // 3. 为每个知识点分配题目
        int questionOrder = 1;
        int totalQuestionsAdded = 0;
        for (Map.Entry<String, Double> entry : knowledgePointWeights.entrySet()) {
            String pointIdStr = entry.getKey();
            Integer pointId = Integer.parseInt(pointIdStr);
            Double weight = entry.getValue();
            System.out.println("处理知识点: " + pointId + ", 权重: " + weight);
            // 计算该知识点的分数
            Double pointScore = totalScore * weight;
            Double remainingScore = pointScore;
            System.out.println("知识点分数: " + pointScore + ", 剩余分数: " + remainingScore);

            // 获取该知识点的所有题目
            List<Question> pointQuestions = questionRepository.findByKpId(pointId);
            System.out.println("该知识点的题目数量: " + pointQuestions.size());
            if (pointQuestions.isEmpty()) {
                System.out.println("该知识点没有题目，跳过");
                continue; // 如果该知识点没有题目，跳过
            }

            // 首先尝试简单的分配方式：随机选择题目直到达到分数要求
            System.out.println("开始随机选择题目，不按难度分布");
            java.util.Collections.shuffle(pointQuestions);
            
            for (Question question : pointQuestions) {
                if (remainingScore <= 0) {
                    break;
                }
                
                // 计算实际分配的分数
                Double actualScore = Math.min(question.getScore(), remainingScore);
                remainingScore -= actualScore;
                totalQuestionsAdded++;

                // 添加题目到试卷
                ExamPaperQuestion examPaperQuestion = new ExamPaperQuestion();
                examPaperQuestion.setPaperId(paper.getPaperId().toString());
                examPaperQuestion.setQuestionId(question.getQuestionId().toString());
                examPaperQuestion.setKpId(pointId);
                examPaperQuestion.setQuestionOrder(questionOrder++);
                examPaperQuestion.setQuestionScore(actualScore);
                examPaperQuestionRepository.save(examPaperQuestion);
                System.out.println("添加题目成功: 题目ID=" + question.getQuestionId() + ", 分数=" + actualScore + ", 剩余分数=" + remainingScore);
            }
        }
        
        System.out.println("总添加题目数量: " + totalQuestionsAdded);
        
        // 如果没有添加任何题目，删除该试卷
        if (totalQuestionsAdded == 0) {
            System.out.println("没有添加任何题目，删除试卷");
            examPaperRepository.deleteById(paper.getPaperId());
            throw new IllegalArgumentException("没有为试卷添加任何题目，无法生成试卷");
        }

        System.out.println("自动组卷成功，返回试卷ID: " + paper.getPaperId());
        return paper.getPaperId().toString();
    }
}
