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
        try {
            System.out.println("开始自动组卷，参数: courseId=" + courseId + ", paperName=" + paperName + ", totalScore=" + totalScore);
            System.out.println("知识点权重: " + knowledgePointWeights);
            System.out.println("难度分布: " + difficultyDistribution);
            
            // 验证参数
            if (knowledgePointWeights == null || knowledgePointWeights.isEmpty()) {
                throw new IllegalArgumentException("知识点权重不能为空");
            }
            if (difficultyDistribution == null || difficultyDistribution.isEmpty()) {
                throw new IllegalArgumentException("难度分布不能为空");
            }
            
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

            // 3. 创建试卷
            ExamPaper paper = new ExamPaper();
            paper.setPaperName(paperName != null && !paperName.isEmpty() ? paperName : "自动组卷 - " + courseId);
            paper.setCourseId(Integer.parseInt(courseId));
            paper.setTotalScore(totalScore);
            paper.setPaperType("自动组卷");
            paper.setCreateTeacher("admin"); // 默认创建教师
            System.out.println("准备保存试卷");
            examPaperRepository.save(paper);
            System.out.println("创建试卷成功，试卷ID: " + paper.getPaperId());

            // 4. 为每个知识点分配题目
            int questionOrder = 1;
            int totalQuestionsAdded = 0;
            for (Map.Entry<String, Double> kpEntry : knowledgePointWeights.entrySet()) {
                String pointIdStr = kpEntry.getKey();
                Integer pointId = Integer.parseInt(pointIdStr);
                Double weight = kpEntry.getValue();
                System.out.println("处理知识点: " + pointId + ", 权重: " + weight);
                // 计算该知识点的分数
                Double pointScore = totalScore * weight;
                Double remainingScore = pointScore;
                System.out.println("知识点分数: " + pointScore + ", 剩余分数: " + remainingScore);

                // 声明变量
                List<Question> pointQuestions = null;
                boolean hasQuestions = false;
                
                try {
                    // 获取该知识点的所有题目
                    pointQuestions = questionRepository.findByKpId(pointId);
                    System.out.println("日志1: 知识点ID=" + pointId + "，题目总数=" + (pointQuestions != null ? pointQuestions.size() : 0));
                    System.out.println("日志2: 知识点权重=" + weight + "，分配分数=" + pointScore);
                    
                    if (pointQuestions == null || pointQuestions.isEmpty()) {
                        System.out.println("日志3: 该知识点没有题目");
                        System.out.println("日志4: 跳过该知识点");
                        System.out.println("日志5: 继续处理下一个知识点");
                        continue; // 如果该知识点没有题目，跳过
                    }
                    
                    System.out.println("日志3: 开始按难度分组题目");
                    System.out.println("日志4: 知识点题目分配开始");
                    System.out.println("日志5: 继续处理该知识点");
                    hasQuestions = true;
                } catch (Exception e) {
                    System.out.println("日志3: 获取题目列表失败，错误信息: " + e.getMessage());
                    System.out.println("日志4: 跳过该知识点");
                    System.out.println("日志5: 继续处理下一个知识点");
                    e.printStackTrace();
                    continue;
                }
                
                // 如果没有题目，跳过
                if (!hasQuestions || pointQuestions == null || pointQuestions.isEmpty()) {
                    continue;
                }
                
                try {
                    // 按难度分组题目
                    Map<String, List<Question>> questionsByDifficulty = new java.util.HashMap<>();
                    for (Question question : pointQuestions) {
                        String difficulty = question.getDifficulty();
                        if (difficulty == null || difficulty.isEmpty()) {
                            difficulty = "一般"; // 默认难度
                        }
                        questionsByDifficulty.computeIfAbsent(difficulty, k -> new java.util.ArrayList<>()).add(question);
                    }
                    
                    // 计算每种难度的目标分数
                    Map<String, Double> difficultyTargetScores = new java.util.HashMap<>();
                    double difficultySum = 0.0;
                    for (Double value : difficultyDistribution.values()) {
                        difficultySum += value != null ? value.doubleValue() : 0.0;
                    }
                    
                    // 难度分布归一化
                    if (Math.abs(difficultySum - 1.0) > 0.02) {
                        for (Map.Entry<String, Double> diffEntry : difficultyDistribution.entrySet()) {
                            String difficulty = diffEntry.getKey();
                            Double diffWeight = diffEntry.getValue() != null ? diffEntry.getValue() : 0.0;
                            Double normalizedDiffWeight = difficultySum > 0 ? diffWeight / difficultySum : 0.0;
                            difficultyTargetScores.put(difficulty, pointScore * normalizedDiffWeight);
                        }
                    } else {
                        for (Map.Entry<String, Double> diffEntry : difficultyDistribution.entrySet()) {
                            String difficulty = diffEntry.getKey();
                            Double diffWeight = diffEntry.getValue() != null ? diffEntry.getValue() : 0.0;
                            difficultyTargetScores.put(difficulty, pointScore * diffWeight);
                        }
                    }
                    
                    // 为每种难度选择题目
                    for (Map.Entry<String, Double> diffEntry : difficultyTargetScores.entrySet()) {
                        String difficulty = diffEntry.getKey();
                        Double targetScore = diffEntry.getValue();
                        Double diffRemainingScore = targetScore;
                        
                        // 获取该难度的题目
                        List<Question> diffQuestions = questionsByDifficulty.getOrDefault(difficulty, new java.util.ArrayList<>());
                        if (diffQuestions.isEmpty()) {
                            continue;
                        }

                        // 打乱题目顺序，增加随机性
                        java.util.Collections.shuffle(diffQuestions);

                        // 选择题目直到达到目标分数
                        for (Question question : diffQuestions) {
                            if (diffRemainingScore <= 0) {
                                break;
                            }
                            
                            try {
                                // 计算实际分配的分数
                                Double actualScore = Math.min(question.getScore(), diffRemainingScore);
                                diffRemainingScore -= actualScore;
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
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }
                    
                    // 如果该知识点还有剩余分数未分配，使用任意难度的题目填充
                    if (remainingScore > 0 && pointQuestions != null && !pointQuestions.isEmpty()) {
                        // 打乱所有题目
                        java.util.Collections.shuffle(pointQuestions);
                        
                        for (Question question : pointQuestions) {
                            if (remainingScore <= 0) {
                                break;
                            }
                            
                            try {
                                Double actualScore = Math.min(question.getScore(), remainingScore);
                                remainingScore -= actualScore;
                                totalQuestionsAdded++;
                                
                                ExamPaperQuestion examPaperQuestion = new ExamPaperQuestion();
                                examPaperQuestion.setPaperId(paper.getPaperId().toString());
                                examPaperQuestion.setQuestionId(question.getQuestionId().toString());
                                examPaperQuestion.setKpId(pointId);
                                examPaperQuestion.setQuestionOrder(questionOrder++);
                                examPaperQuestion.setQuestionScore(actualScore);
                                examPaperQuestionRepository.save(examPaperQuestion);
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("处理题目分配失败，错误信息: " + e.getMessage());
                    e.printStackTrace();
                    continue;
                }
            }
            
            // 如果没有添加任何题目，删除该试卷
            if (totalQuestionsAdded == 0) {
                System.out.println("没有添加任何题目，删除试卷");
                examPaperRepository.deleteById(paper.getPaperId());
                throw new IllegalArgumentException("没有为试卷添加任何题目，无法生成试卷");
            }

            System.out.println("自动组卷成功，返回试卷ID: " + paper.getPaperId());
            return paper.getPaperId().toString();
        } catch (Exception e) {
            System.out.println("自动组卷失败，错误信息: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}