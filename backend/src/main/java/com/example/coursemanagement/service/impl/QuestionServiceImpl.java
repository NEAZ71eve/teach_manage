package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.Question;
import com.example.coursemanagement.entity.QuestionCategory;
import com.example.coursemanagement.entity.QuestionTag;
import com.example.coursemanagement.repository.ExamPaperQuestionRepository;
import com.example.coursemanagement.repository.QuestionCategoryRepository;
import com.example.coursemanagement.repository.QuestionRepository;
import com.example.coursemanagement.repository.QuestionTagRepository;
import com.example.coursemanagement.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 题目Service实现类
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamPaperQuestionRepository examPaperQuestionRepository;
    
    @Autowired
    private QuestionCategoryRepository questionCategoryRepository;
    
    @Autowired
    private QuestionTagRepository questionTagRepository;

    @Override
    public List<Question> list() {
        return questionRepository.findAll();
    }

    @Override
    public Question getById(Integer id) {
        return questionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Question> listByCourseId(Integer courseId) {
        return questionRepository.findByCourseId(courseId);
    }

    @Override
    public List<Question> listByKpId(Integer kpId) {
        return questionRepository.findByKpId(kpId);
    }

    @Override
    public List<Question> listByType(Integer questionType) {
        return questionRepository.findByType(questionType);
    }

    @Override
    public List<Question> listByDifficulty(String difficulty) {
        return questionRepository.findByDifficulty(difficulty);
    }
    
    @Override
    public boolean save(Question question) {
        System.out.println("=== 开始保存题目 ===");
        System.out.println("题目类型: " + question.getQuestionType());
        System.out.println("题目内容: " + question.getQuestionContent());
        System.out.println("分类ID: " + question.getCategoryId());
        System.out.println("知识点ID: " + question.getKpId());
        System.out.println("难度: " + question.getDifficulty());
        System.out.println("分值: " + question.getScore());
        System.out.println("正确答案: " + question.getCorrectAnswer());
        System.out.println("状态: " + question.getStatus());
        System.out.println("创建者ID: " + question.getCreatorId());
        System.out.println("选项数量: " + (question.getOptions() != null ? question.getOptions().size() : 0));
        System.out.println("标签数量: " + (question.getTags() != null ? question.getTags().size() : 0));
        
        try {
            int result = questionRepository.save(question);
            System.out.println("保存结果: " + result);
            System.out.println("=== 保存题目结束 ===");
            return result > 0;
        } catch (Exception e) {
            System.out.println("保存题目异常: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== 保存题目异常结束 ===");
            throw e;
        }
    }

    @Override
    public boolean updateById(Question question) {
        return questionRepository.update(question) > 0;
    }

    @Override
    public boolean removeById(Integer id) {
        // 检查该题目是否已用于组卷
        List<?> examPaperQuestions = examPaperQuestionRepository.findByQuestionId(id.toString());
        if (!examPaperQuestions.isEmpty()) {
            throw new IllegalArgumentException("该题目已用于组卷，不可删除");
        }
        return questionRepository.deleteById(id) > 0;
    }

    @Override
    public boolean removeByIds(List<Integer> ids) {
        // 检查所有题目是否已用于组卷
        for (Integer id : ids) {
            List<?> examPaperQuestions = examPaperQuestionRepository.findByQuestionId(id.toString());
            if (!examPaperQuestions.isEmpty()) {
                throw new IllegalArgumentException("部分题目已用于组卷，不可批量删除");
            }
        }
        return questionRepository.deleteBatch(ids) > 0;
    }

    @Override
    public List<Question> listPage(Integer page, Integer limit, Integer questionType, String difficulty, Integer kpId, Integer courseId, String keyword) {
        return questionRepository.findByPage(page, limit, questionType, difficulty, kpId, courseId, keyword);
    }

    @Override
    public int count(Integer questionType, String difficulty, Integer kpId, Integer courseId, String keyword) {
        return questionRepository.count(questionType, difficulty, kpId, courseId, keyword);
    }
    
    @Override
    public List<QuestionCategory> listCategories() {
        return questionCategoryRepository.findAll();
    }
    
    @Override
    public List<QuestionTag> listTags() {
        return questionTagRepository.findAll();
    }
    
    @Override
    public boolean updateUsedStatus(Integer questionId, Integer isUsed) {
        // 更新题目使用状态
        String sql = "UPDATE question SET is_used = ? WHERE question_id = ?";
        return questionRepository.updateUsedStatus(sql, isUsed, questionId) > 0;
    }
    
    @Override
    public boolean reviewQuestion(Integer questionId, String status, Integer reviewerId, String remark) {
        // 检查题目是否存在
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return false;
        }
        
        // 更新题目审核状态
        question.setStatus(status);
        question.setReviewerId(reviewerId);
        question.setReviewRemark(remark);
        
        return questionRepository.update(question) > 0;
    }
}
