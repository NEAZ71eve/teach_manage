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
        List<ExamPaperQuestion> paperQuestions = examPaperQuestionRepository.findByPaperId(paperId);
        // 为每个试卷题目关联查询完整的题目信息
        for (ExamPaperQuestion paperQuestion : paperQuestions) {
            Integer questionId = Integer.parseInt(paperQuestion.getQuestionId());
            Question question = questionRepository.findById(questionId).orElse(null);
            paperQuestion.setQuestion(question);
        }
        return paperQuestions;
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
        System.out.println("=== 开始自动组卷流程 ===");
        try {
            System.out.println("1. 接收到组卷请求，参数: courseId=" + courseId + ", paperName=" + paperName + ", totalScore=" + totalScore);
            System.out.println("1.1 知识点权重: " + knowledgePointWeights);
            System.out.println("1.2 难度分布: " + difficultyDistribution);
            
            // 验证参数
            if (knowledgePointWeights == null || knowledgePointWeights.isEmpty()) {
                System.out.println("1.3 知识点权重为空，抛出异常");
                throw new IllegalArgumentException("知识点权重不能为空");
            }
            if (difficultyDistribution == null || difficultyDistribution.isEmpty()) {
                System.out.println("1.4 难度分布为空，抛出异常");
                throw new IllegalArgumentException("难度分布不能为空");
            }
            
            // 1. 计算知识点权重总和
            System.out.println("2. 开始计算知识点权重总和");
            double weightSum = 0.0;
            for (Double value : knowledgePointWeights.values()) {
                weightSum += value != null ? value.doubleValue() : 0.0;
            }
            System.out.println("2.1 原始知识点权重总和: " + weightSum);
            
            // 2. 权重归一化处理：如果总和不等于1，自动调整为1
            if (Math.abs(weightSum - 1.0) > 0.02) {
                System.out.println("3. 权重总和不等于1，开始归一化处理");
                System.out.println("3.1 权重归一化处理前: " + knowledgePointWeights);
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
                System.out.println("3.2 权重归一化处理后，总和: " + weightSum);
                System.out.println("3.3 归一化后的权重: " + knowledgePointWeights);
            }

            // 3. 创建试卷
            System.out.println("4. 开始创建试卷");
            ExamPaper paper = new ExamPaper();
            paper.setPaperName(paperName != null && !paperName.isEmpty() ? paperName : "自动组卷 - " + courseId);
            System.out.println("4.1 设置试卷名称: " + paper.getPaperName());
            
            Integer courseIdInt = null;
            try {
                courseIdInt = Integer.parseInt(courseId);
                paper.setCourseId(courseIdInt);
                System.out.println("4.2 设置课程ID: " + courseIdInt);
            } catch (NumberFormatException e) {
                System.out.println("4.2 课程ID格式错误: " + courseId);
                throw new IllegalArgumentException("课程ID格式错误");
            }
            
            // 检查课程是否存在
            try {
                System.out.println("4.2.1 检查课程是否存在");
                // 这里假设我们有一个CourseRepository可以查询课程是否存在
                // 由于我们没有直接访问CourseRepository，我们可以尝试查询该课程的知识点
                // 如果没有知识点，可能是课程不存在，或者该课程下没有知识点
                List<Question> courseQuestions = questionRepository.findByCourseId(courseIdInt);
                System.out.println("4.2.2 课程下的题目数量: " + (courseQuestions != null ? courseQuestions.size() : 0));
                // 不需要抛出异常，只需要记录日志
            } catch (Exception e) {
                System.out.println("4.2.3 检查课程是否存在时发生错误: " + e.getMessage());
                // 不需要抛出异常，只需要记录日志
            }
            
            paper.setTotalScore(totalScore);
            System.out.println("4.3 设置总分: " + totalScore);
            paper.setPaperType("自动组卷");
            paper.setCreateTeacher("admin"); // 默认创建教师
            System.out.println("4.4 准备保存试卷到数据库");
            
            int saveResult;
            try {
                saveResult = examPaperRepository.save(paper);
                System.out.println("4.5 保存试卷结果: " + saveResult);
                System.out.println("4.6 创建试卷成功，试卷ID: " + paper.getPaperId());
            } catch (Exception e) {
                System.out.println("4.5 保存试卷失败，详细错误信息:");
                System.out.println("错误类型: " + e.getClass().getName());
                System.out.println("错误信息: " + e.getMessage());
                System.out.println("完整堆栈:");
                e.printStackTrace();
                throw e;
            }

            // 4. 为每个知识点分配题目
            System.out.println("5. 开始为知识点分配题目");
            int questionOrder = 1;
            int totalQuestionsAdded = 0;
            
            for (Map.Entry<String, Double> kpEntry : knowledgePointWeights.entrySet()) {
                String pointIdStr = kpEntry.getKey();
                System.out.println("5.1 处理知识点: " + pointIdStr);
                
                Integer pointId = null;
                try {
                    pointId = Integer.parseInt(pointIdStr);
                } catch (NumberFormatException e) {
                    System.out.println("5.1.1 知识点ID格式错误: " + pointIdStr);
                    continue;
                }
                
                Double weight = kpEntry.getValue();
                System.out.println("5.1.2 知识点ID: " + pointId + ", 权重: " + weight);
                
                // 计算该知识点的分数
                Double pointScore = totalScore * weight;
                Double remainingScore = pointScore;
                System.out.println("5.1.3 知识点分数: " + pointScore + ", 剩余分数: " + remainingScore);

                // 声明变量
                List<Question> pointQuestions = null;
                boolean hasQuestions = false;
                
                try {
                    System.out.println("5.2 开始查询知识点ID为" + pointId + "的题目");
                    // 获取该知识点的所有题目
                    pointQuestions = questionRepository.findByKpId(pointId);
                    System.out.println("5.2.1 查询结果: " + (pointQuestions != null ? pointQuestions.size() : 0) + "道题目");
                    System.out.println("日志1: 知识点ID=" + pointId + "，题目总数=" + (pointQuestions != null ? pointQuestions.size() : 0));
                    System.out.println("日志2: 知识点权重=" + weight + "，分配分数=" + pointScore);
                    
                    if (pointQuestions == null || pointQuestions.isEmpty()) {
                        System.out.println("5.2.2 该知识点没有题目");
                        System.out.println("日志3: 该知识点没有题目");
                        System.out.println("日志4: 跳过该知识点");
                        System.out.println("日志5: 继续处理下一个知识点");
                        continue; // 如果该知识点没有题目，跳过
                    }
                    
                    System.out.println("5.2.3 开始处理该知识点的" + pointQuestions.size() + "道题目");
                    System.out.println("日志3: 开始按难度分组题目");
                    System.out.println("日志4: 知识点题目分配开始");
                    System.out.println("日志5: 继续处理该知识点");
                    hasQuestions = true;
                } catch (Exception e) {
                    System.out.println("5.2.4 获取题目列表失败，错误信息: " + e.getMessage());
                    System.out.println("日志3: 获取题目列表失败，错误信息: " + e.getMessage());
                    System.out.println("日志4: 跳过该知识点");
                    System.out.println("日志5: 继续处理下一个知识点");
                    e.printStackTrace();
                    continue;
                }
                
                // 如果没有题目，跳过
                if (!hasQuestions || pointQuestions == null || pointQuestions.isEmpty()) {
                    System.out.println("5.3 跳过该知识点，没有可用题目");
                    continue;
                }
                
                try {
                    System.out.println("5.4 开始按难度分组题目");
                    // 按难度分组题目
                    Map<String, List<Question>> questionsByDifficulty = new java.util.HashMap<>();
                    for (Question question : pointQuestions) {
                        String difficulty = question.getDifficulty();
                        if (difficulty == null || difficulty.isEmpty()) {
                            difficulty = "中等"; // 默认难度
                        }
                        questionsByDifficulty.computeIfAbsent(difficulty, k -> new java.util.ArrayList<>()).add(question);
                    }
                    System.out.println("5.4.1 按难度分组完成，分组结果: " + questionsByDifficulty);
                    
                    // 计算每种难度的目标分数
                    System.out.println("5.5 开始计算每种难度的目标分数");
                    Map<String, Double> difficultyTargetScores = new java.util.HashMap<>();
                    double difficultySum = 0.0;
                    for (Double value : difficultyDistribution.values()) {
                        difficultySum += value != null ? value.doubleValue() : 0.0;
                    }
                    System.out.println("5.5.1 原始难度分布总和: " + difficultySum);
                    
                    // 难度分布归一化
                    if (Math.abs(difficultySum - 1.0) > 0.02) {
                        System.out.println("5.6 难度分布总和不等于1，开始归一化处理");
                        for (Map.Entry<String, Double> diffEntry : difficultyDistribution.entrySet()) {
                            String difficulty = diffEntry.getKey();
                            Double diffWeight = diffEntry.getValue() != null ? diffEntry.getValue() : 0.0;
                            Double normalizedDiffWeight = difficultySum > 0 ? diffWeight / difficultySum : 0.0;
                            difficultyTargetScores.put(difficulty, pointScore * normalizedDiffWeight);
                        }
                    } else {
                        System.out.println("5.6 难度分布总和为1，不需要归一化");
                        for (Map.Entry<String, Double> diffEntry : difficultyDistribution.entrySet()) {
                            String difficulty = diffEntry.getKey();
                            Double diffWeight = diffEntry.getValue() != null ? diffEntry.getValue() : 0.0;
                            difficultyTargetScores.put(difficulty, pointScore * diffWeight);
                        }
                    }
                    System.out.println("5.7 难度目标分数计算完成: " + difficultyTargetScores);
                    
                    // 为每种难度选择题目
                    System.out.println("5.8 开始为每种难度选择题目");
                    for (Map.Entry<String, Double> diffEntry : difficultyTargetScores.entrySet()) {
                        String difficulty = diffEntry.getKey();
                        Double targetScore = diffEntry.getValue();
                        Double diffRemainingScore = targetScore;
                        System.out.println("5.8.1 处理难度: " + difficulty + ", 目标分数: " + targetScore);
                        
                        // 获取该难度的题目
                        List<Question> diffQuestions = questionsByDifficulty.getOrDefault(difficulty, new java.util.ArrayList<>());
                        System.out.println("5.8.2 该难度可用题目数量: " + diffQuestions.size());
                        if (diffQuestions.isEmpty()) {
                            System.out.println("5.8.3 该难度没有可用题目，跳过");
                            continue;
                        }

                        // 打乱题目顺序，增加随机性
                        java.util.Collections.shuffle(diffQuestions);
                        System.out.println("5.8.4 打乱题目顺序");

                        // 选择题目直到达到目标分数
                        System.out.println("5.8.5 开始选择题目，剩余分数: " + diffRemainingScore);
                        for (Question question : diffQuestions) {
                            if (diffRemainingScore <= 0) {
                                System.out.println("5.8.6 已达到目标分数，停止选择");
                                break;
                            }
                            
                            try {
                                System.out.println("5.8.7 处理题目: ID=" + question.getQuestionId() + ", 难度=" + question.getDifficulty() + ", 分数=" + question.getScore());
                                // 计算实际分配的分数
                                Double actualScore = Math.min(question.getScore(), diffRemainingScore);
                                
                                // 添加题目到试卷
                                System.out.println("5.8.9 添加题目到试卷");
                                ExamPaperQuestion examPaperQuestion = new ExamPaperQuestion();
                                examPaperQuestion.setPaperId(paper.getPaperId().toString());
                                examPaperQuestion.setQuestionId(question.getQuestionId().toString());
                                examPaperQuestion.setQuestionOrder(questionOrder++);
                                
                                System.out.println("5.8.10 保存试卷题目关联");
                                int questionSaveResult = examPaperQuestionRepository.save(examPaperQuestion);
                                if (questionSaveResult > 0) {
                                    // 只有保存成功后才更新分数和计数器
                                    diffRemainingScore -= actualScore;
                                    remainingScore -= actualScore;
                                    totalQuestionsAdded++;
                                    System.out.println("5.8.11 保存成功，分配分数: " + actualScore + ", 剩余分数: " + diffRemainingScore);
                                } else {
                                    System.out.println("5.8.11 保存失败");
                                }
                            } catch (Exception e) {
                                System.out.println("5.8.12 处理题目失败，错误信息: " + e.getMessage());
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }
                    
                    // 如果该知识点还有剩余分数未分配，使用任意难度的题目填充
                    if (remainingScore > 0 && pointQuestions != null && !pointQuestions.isEmpty()) {
                        System.out.println("5.9 该知识点还有剩余分数: " + remainingScore + "，开始填充");
                        // 打乱所有题目
                        java.util.Collections.shuffle(pointQuestions);
                        
                        for (Question question : pointQuestions) {
                            if (remainingScore <= 0) {
                                System.out.println("5.9.1 已达到目标分数，停止填充");
                                break;
                            }
                            
                            try {
                                System.out.println("5.9.2 处理填充题目: ID=" + question.getQuestionId() + ", 分数=" + question.getScore());
                                Double actualScore = Math.min(question.getScore(), remainingScore);
                                
                                ExamPaperQuestion examPaperQuestion = new ExamPaperQuestion();
                                examPaperQuestion.setPaperId(paper.getPaperId().toString());
                                examPaperQuestion.setQuestionId(question.getQuestionId().toString());
                                examPaperQuestion.setQuestionOrder(questionOrder++);
                                
                                System.out.println("5.9.3 保存填充题目关联");
                                int fillSaveResult = examPaperQuestionRepository.save(examPaperQuestion);
                                if (fillSaveResult > 0) {
                                    // 只有保存成功后才更新分数和计数器
                                    remainingScore -= actualScore;
                                    totalQuestionsAdded++;
                                    System.out.println("5.9.4 保存成功");
                                } else {
                                    System.out.println("5.9.4 保存失败");
                                }
                            } catch (Exception e) {
                                System.out.println("5.9.5 处理填充题目失败，错误信息: " + e.getMessage());
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }
                    System.out.println("5.10 该知识点题目分配完成");
                } catch (Exception e) {
                    System.out.println("5.11 处理题目分配失败，错误信息: " + e.getMessage());
                    e.printStackTrace();
                    continue;
                }
            }
            
            // 如果没有添加任何题目，删除该试卷
            System.out.println("6. 组卷完成，总共添加了" + totalQuestionsAdded + "道题目");
            if (totalQuestionsAdded == 0) {
                System.out.println("6.1 没有添加任何题目，删除试卷");
                examPaperRepository.deleteById(paper.getPaperId());
                throw new IllegalArgumentException("没有为试卷添加任何题目，无法生成试卷");
            } else {
                System.out.println("6.1 成功添加了" + totalQuestionsAdded + "道题目到试卷中");
            }

            System.out.println("7. 自动组卷成功，返回试卷ID: " + paper.getPaperId());
            return paper.getPaperId().toString();
        } catch (Exception e) {
            System.out.println("=== 自动组卷流程失败 ===");
            System.out.println("错误类型: " + e.getClass().getName());
            System.out.println("错误信息: " + e.getMessage());
            System.out.println("错误堆栈:");
            e.printStackTrace();
            throw e;
        } finally {
            System.out.println("=== 自动组卷流程结束 ===");
        }
    }
}
