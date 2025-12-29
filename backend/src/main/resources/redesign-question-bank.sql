-- 题库管理系统重新设计
-- 1. 优化知识点表
ALTER TABLE knowledge_point 
ADD COLUMN kp_order INT DEFAULT 0 COMMENT '知识点顺序',
ADD COLUMN is_active TINYINT DEFAULT 1 COMMENT '是否激活',
ADD INDEX idx_kp_active (is_active);

-- 2. 新增题目分类表
CREATE TABLE IF NOT EXISTS question_category (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    parent_category_id INT COMMENT '父分类ID',
    category_order INT DEFAULT 0 COMMENT '分类顺序',
    is_active TINYINT DEFAULT 1 COMMENT '是否激活',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES question_category(category_id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_category_parent (parent_category_id),
    INDEX idx_category_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目分类表';

-- 3. 重新设计题目表
DROP TABLE IF EXISTS question;
CREATE TABLE IF NOT EXISTS question (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    question_type INT NOT NULL COMMENT '题目类型：1-单选题，2-多选题，3-判断题，4-填空题，5-简答题',
    question_content TEXT NOT NULL COMMENT '题目内容',
    category_id INT COMMENT '题目分类',
    kp_id INT NOT NULL COMMENT '所属知识点',
    difficulty ENUM('简单', '中等', '困难') DEFAULT '中等' COMMENT '题目难度',
    score DECIMAL(3,1) NOT NULL COMMENT '题目分值',
    correct_answer TEXT NOT NULL COMMENT '正确答案',
    analysis TEXT COMMENT '答案解析',
    is_used TINYINT DEFAULT 0 COMMENT '是否被使用：0-未使用，1-已使用',
    status ENUM('待审核', '已审核', '已拒绝') DEFAULT '待审核' COMMENT '题目状态',
    creator_id INT COMMENT '创建者ID',
    reviewer_id INT COMMENT '审核者ID',
    review_time DATETIME COMMENT '审核时间',
    review_remark TEXT COMMENT '审核备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES question_category(category_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (kp_id) REFERENCES knowledge_point(kp_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (creator_id) REFERENCES `user`(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES `user`(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_question_category (category_id),
    INDEX idx_question_kp (kp_id),
    INDEX idx_question_type (question_type),
    INDEX idx_question_status (status),
    INDEX idx_question_difficulty (difficulty),
    INDEX idx_question_creator (creator_id),
    INDEX idx_question_reviewer (reviewer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 4. 新增题目选项表（支持灵活的选项数量）
CREATE TABLE IF NOT EXISTS question_option (
    option_id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL COMMENT '所属题目',
    option_text VARCHAR(500) NOT NULL COMMENT '选项内容',
    option_order INT NOT NULL COMMENT '选项顺序',
    is_correct TINYINT DEFAULT 0 COMMENT '是否为正确选项：0-否，1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_option_question (question_id),
    INDEX idx_option_correct (is_correct)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';

-- 5. 新增题目使用统计表
CREATE TABLE IF NOT EXISTS question_statistics (
    stat_id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL COMMENT '题目ID',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    correct_count INT DEFAULT 0 COMMENT '答对次数',
    incorrect_count INT DEFAULT 0 COMMENT '答错次数',
    correct_rate DECIMAL(5,2) DEFAULT 0 COMMENT '正确率',
    last_used_time DATETIME COMMENT '最后使用时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_stat_question (question_id),
    INDEX idx_stat_correct_rate (correct_rate),
    INDEX idx_stat_last_used (last_used_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目使用统计表';

-- 6. 新增题目标签表
CREATE TABLE IF NOT EXISTS question_tag (
    tag_id INT PRIMARY KEY AUTO_INCREMENT,
    tag_name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    tag_description TEXT COMMENT '标签描述',
    is_active TINYINT DEFAULT 1 COMMENT '是否激活',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tag_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目标签表';

-- 7. 新增题目-标签关联表
CREATE TABLE IF NOT EXISTS question_tag_relation (
    relation_id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL COMMENT '题目ID',
    tag_id INT NOT NULL COMMENT '标签ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES question_tag(tag_id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_question_tag (question_id, tag_id),
    INDEX idx_relation_question (question_id),
    INDEX idx_relation_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目-标签关联表';

-- 8. 优化试卷题目关联表
ALTER TABLE exam_paper_question 
ADD COLUMN question_score DECIMAL(3,1) NOT NULL COMMENT '题目在试卷中的实际分值',
ADD COLUMN question_order_in_paper INT NOT NULL COMMENT '题目在试卷中的顺序',
ADD INDEX idx_paper_question_order (question_order_in_paper);

-- 9. 新增初始数据
-- 插入题目分类
INSERT INTO question_category (category_name, description, category_order) VALUES 
('基础知识', '课程基础知识题目', 1),
('应用能力', '应用能力题目', 2),
('综合分析', '综合分析题目', 3);

-- 插入题目标签
INSERT INTO question_tag (tag_name, tag_description) VALUES 
('高频考点', '考试高频出现的考点'),
('易错题目', '学生容易出错的题目'),
('重点难点', '课程重点难点题目'),
('经典例题', '经典例题'),
('新知识点', '新增加的知识点题目');

-- 插入完成提示
SELECT '题库管理系统重新设计完成！' AS message;