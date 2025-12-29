package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.Question;
import com.example.coursemanagement.repository.ExamPaperQuestionRepository;
import com.example.coursemanagement.repository.QuestionRepository;
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
    public List<Question> listByPointId(String pointId) {
        return questionRepository.findByPointId(Integer.parseInt(pointId));
    }

    @Override
    public List<Question> listByType(String questionType) {
        return questionRepository.findByType(questionType);
    }

    @Override
    public List<Question> listByDifficulty(String difficulty) {
        return questionRepository.findByDifficulty(difficulty);
    }

    @Override
    public boolean save(Question question) {
        return questionRepository.save(question) > 0;
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
    public List<Question> listPage(Integer page, Integer limit) {
        return questionRepository.findByPage(page, limit);
    }

    @Override
    public int count() {
        return questionRepository.count();
    }
}
