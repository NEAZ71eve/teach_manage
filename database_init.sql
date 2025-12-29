-- 题库管理系统数据库初始化脚本
-- 数据库名：teach_manage

-- 1. 创建题目分类表（question_category）
CREATE TABLE IF NOT EXISTS question_category (
    category_id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    parent_category_id INT(11) COMMENT '父分类ID，用于树形结构',
    category_order INT(11) NOT NULL DEFAULT 0 COMMENT '分类排序',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活（1-激活，0-禁用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (parent_category_id) REFERENCES question_category(category_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目分类表';

-- 2. 创建知识点表（knowledge_point）
CREATE TABLE IF NOT EXISTS knowledge_point (
    point_id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '知识点ID',
    point_name VARCHAR(100) NOT NULL COMMENT '知识点名称',
    course_id INT(11) COMMENT '所属课程ID',
    parent_id INT(11) COMMENT '父知识点ID，用于树形结构',
    description TEXT COMMENT '知识点描述',
    difficulty VARCHAR(20) COMMENT '知识点难度（简单、中等、困难）',
    kp_order INT(11) NOT NULL DEFAULT 0 COMMENT '知识点排序',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活（1-激活，0-禁用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES knowledge_point(point_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- 3. 创建题目表（question）
CREATE TABLE IF NOT EXISTS question (
    question_id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    question_type TINYINT(1) NOT NULL COMMENT '题目类型（1-单选题，2-多选题，3-判断题，4-填空题，5-简答题）',
    question_content TEXT NOT NULL COMMENT '题目内容',
    category_id INT(11) COMMENT '所属分类ID',
    point_id INT(11) COMMENT '所属知识点ID',
    difficulty VARCHAR(20) NOT NULL COMMENT '题目难度（简单、中等、困难）',
    score DOUBLE NOT NULL COMMENT '题目分数',
    correct_answer TEXT NOT NULL COMMENT '正确答案',
    analysis TEXT COMMENT '答案解析',
    is_used TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否被使用（0-未使用，1-已使用）',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '题目状态（draft-草稿，review-审核中，published-已发布，rejected-已拒绝）',
    creator_id INT(11) COMMENT '创建者ID',
    reviewer_id INT(11) COMMENT '审核者ID',
    review_time DATETIME COMMENT '审核时间',
    review_remark TEXT COMMENT '审核备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES question_category(category_id) ON DELETE SET NULL,
    FOREIGN KEY (point_id) REFERENCES knowledge_point(point_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 4. 创建题目选项表（question_option）
CREATE TABLE IF NOT EXISTS question_option (
    option_id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '选项ID',
    question_id INT(11) NOT NULL COMMENT '所属题目ID',
    option_text TEXT NOT NULL COMMENT '选项内容',
    option_order INT(11) NOT NULL COMMENT '选项顺序',
    is_correct TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否正确选项（0-错误，1-正确）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';

-- 5. 创建题目标签表（question_tag）
CREATE TABLE IF NOT EXISTS question_tag (
    tag_id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    tag_name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    tag_description TEXT COMMENT '标签描述',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活（1-激活，0-禁用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目标签表';

-- 6. 创建题目-标签关联表（question_tag_relation）
CREATE TABLE IF NOT EXISTS question_tag_relation (
    relation_id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    question_id INT(11) NOT NULL COMMENT '题目ID',
    tag_id INT(11) NOT NULL COMMENT '标签ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES question_tag(tag_id) ON DELETE CASCADE,
    UNIQUE KEY uk_question_tag (question_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目-标签关联表';

-- 7. 创建题目统计表（question_statistics）
CREATE TABLE IF NOT EXISTS question_statistics (
    stat_id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    question_id INT(11) NOT NULL COMMENT '题目ID',
    usage_count INT(11) NOT NULL DEFAULT 0 COMMENT '使用次数',
    correct_count INT(11) NOT NULL DEFAULT 0 COMMENT '答对次数',
    incorrect_count INT(11) NOT NULL DEFAULT 0 COMMENT '答错次数',
    correct_rate DOUBLE NOT NULL DEFAULT 0 COMMENT '正确率',
    last_used_time DATETIME COMMENT '最后使用时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目统计表';

-- 8. 创建题目导入导出日志表（question_import_export_log）
CREATE TABLE IF NOT EXISTS question_import_export_log (
    log_id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型（import-导入，export-导出）',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    record_count INT(11) NOT NULL COMMENT '记录数量',
    success_count INT(11) NOT NULL COMMENT '成功数量',
    failure_count INT(11) NOT NULL COMMENT '失败数量',
    operator_id INT(11) COMMENT '操作人ID',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    remark TEXT COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目导入导出日志表';

-- 9. 索引将在表创建时自动处理，或通过管理工具手动创建


-- 10. 插入初始数据
-- 初始题目分类
INSERT IGNORE INTO question_category (category_name, description, category_order) VALUES 
('计算机基础', '计算机相关基础知识', 1),
('编程语言', '各种编程语言相关题目', 2),
('数据库', '数据库相关题目', 3),
('网络技术', '网络相关题目', 4),
('软件工程', '软件工程相关题目', 5);

-- 初始题目标签
INSERT IGNORE INTO question_tag (tag_name, tag_description) VALUES 
('基础概念', '基础知识概念题'),
('编程实践', '编程实践相关题目'),
('算法设计', '算法设计相关题目'),
('数据库设计', '数据库设计相关题目'),
('网络协议', '网络协议相关题目'),
('软件工程', '软件工程相关题目');

-- 11. 创建视图，用于方便查询题目信息
CREATE VIEW v_question_info AS
SELECT 
    q.question_id,
    q.question_type,
    q.question_content,
    q.category_id,
    qc.category_name,
    q.point_id,
    kp.point_name,
    q.difficulty,
    q.score,
    q.correct_answer,
    q.analysis,
    q.is_used,
    q.status,
    q.creator_id,
    q.reviewer_id,
    q.review_time,
    q.review_remark,
    q.create_time,
    q.update_time,
    stat.usage_count,
    stat.correct_count,
    stat.incorrect_count,
    stat.correct_rate
FROM question q
LEFT JOIN question_category qc ON q.category_id = qc.category_id
LEFT JOIN knowledge_point kp ON q.point_id = kp.point_id
LEFT JOIN question_statistics stat ON q.question_id = stat.question_id;

-- 12. 创建存储过程，用于批量更新题目状态
DELIMITER //
CREATE PROCEDURE batch_update_question_status(IN new_status VARCHAR(20), IN question_ids VARCHAR(1000))
BEGIN
    SET @sql = CONCAT('UPDATE question SET status = ? WHERE question_id IN (', question_ids, ')');
    PREPARE stmt FROM @sql;
    EXECUTE stmt USING new_status;
    DEALLOCATE PREPARE stmt;
END //
DELIMITER ;

-- 13. 创建存储过程，用于统计题目使用情况
DELIMITER //
CREATE PROCEDURE get_question_statistics(IN start_date DATETIME, IN end_date DATETIME)
BEGIN
    SELECT 
        q.question_id,
        q.question_content,
        q.question_type,
        q.difficulty,
        qc.category_name,
        kp.point_name,
        stat.usage_count,
        stat.correct_count,
        stat.incorrect_count,
        stat.correct_rate
    FROM question q
    LEFT JOIN question_category qc ON q.category_id = qc.category_id
    LEFT JOIN knowledge_point kp ON q.point_id = kp.point_id
    LEFT JOIN question_statistics stat ON q.question_id = stat.question_id
    WHERE stat.last_used_time BETWEEN start_date AND end_date
    ORDER BY stat.usage_count DESC;
END //
DELIMITER ;

-- 14. 创建触发器，自动更新题目使用状态
DELIMITER //
CREATE TRIGGER trg_question_usage_update AFTER UPDATE ON question_statistics
FOR EACH ROW
BEGIN
    IF NEW.usage_count > 0 AND OLD.usage_count = 0 THEN
        UPDATE question SET is_used = 1 WHERE question_id = NEW.question_id;
    END IF;
    IF NEW.usage_count = 0 AND OLD.usage_count > 0 THEN
        UPDATE question SET is_used = 0 WHERE question_id = NEW.question_id;
    END IF;
    -- 更新正确率
    IF NEW.usage_count > 0 THEN
        UPDATE question_statistics SET correct_rate = NEW.correct_count / NEW.usage_count WHERE question_id = NEW.question_id;
    END IF;
END //
DELIMITER ;

-- 15. 创建触发器，在插入新题目时自动创建统计记录
DELIMITER //
CREATE TRIGGER trg_question_after_insert AFTER INSERT ON question
FOR EACH ROW
BEGIN
    INSERT INTO question_statistics (question_id) VALUES (NEW.question_id);
END //
DELIMITER ;

-- 数据库初始化完成
SELECT '数据库初始化完成' AS message;
