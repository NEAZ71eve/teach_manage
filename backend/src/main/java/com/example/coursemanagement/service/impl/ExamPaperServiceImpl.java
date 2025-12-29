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
    public String autoGeneratePaper(String courseId, Double totalScore, Map<String, Double> knowledgePointWeights, Map<String, Double> difficultyDistribution) {
        // 1. 验证知识点权重总和是否等于100%
        double weightSum = knowledgePointWeights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(weightSum - 1.0) > 0.01) {
            throw new IllegalArgumentException("知识点权重总和必须等于100%");
        }

        // 2. 创建试卷
        ExamPaper paper = new ExamPaper();
        paper.setPaperName("自动组卷 - " + courseId);
        paper.setCourseId(Integer.parseInt(courseId));
        paper.setTotalScore(totalScore);
        paper.setPaperType("自动组卷");
        examPaperRepository.save(paper);

        // 3. 为每个知识点分配题目
        int questionOrder = 1;
        for (Map.Entry<String, Double> entry : knowledgePointWeights.entrySet()) {
            String pointIdStr = entry.getKey();
            Integer pointId = Integer.parseInt(pointIdStr);
            Double weight = entry.getValue();
            // 计算该知识点的分数
            Double pointScore = totalScore * weight;

            // 获取该知识点的所有题目
            List<Question> pointQuestions = questionRepository.findByPointId(pointId);
            if (pointQuestions.isEmpty()) {
                continue; // 如果该知识点没有题目，跳过
            }

            // 根据难度分布选择题目
            Map<String, List<Question>> questionsByDifficulty = pointQuestions.stream()
                    .collect(Collectors.groupingBy(Question::getDifficulty));

            for (Map.Entry<String, Double> diffEntry : difficultyDistribution.entrySet()) {
                String difficulty = diffEntry.getKey();
                Double diffWeight = diffEntry.getValue();
                // 计算该难度下的分数
                Double diffScore = pointScore * diffWeight;

                List<Question> diffQuestions = questionsByDifficulty.getOrDefault(difficulty, new ArrayList<>());
                if (diffQuestions.isEmpty()) {
                    continue; // 如果该难度没有题目，跳过
                }

                // 按分数选择题目，这里简单实现为随机选择
                // 实际应该根据题目分值、数量等因素进行更复杂的选择
                int selectedCount = (int) Math.ceil(diffScore / 5.0); // 假设每题5分
                selectedCount = Math.min(selectedCount, diffQuestions.size());

                for (int i = 0; i < selectedCount; i++) {
                    Question question = diffQuestions.get(i);
                    // 计算实际分配的分数
                    Double actualScore = Math.min(question.getScore(), diffScore);
                    diffScore -= actualScore;

                    // 添加题目到试卷
                    ExamPaperQuestion examPaperQuestion = new ExamPaperQuestion();
                    examPaperQuestion.setPaperId(paper.getPaperId().toString());
                    examPaperQuestion.setQuestionId(question.getQuestionId().toString());
                    examPaperQuestion.setQuestionOrder(questionOrder++);
                    examPaperQuestion.setScore(actualScore);
                    examPaperQuestionRepository.save(examPaperQuestion);

                    if (diffScore <= 0) {
                        break;
                    }
                }
            }
        }

        return paper.getPaperId().toString();
    }
}
