-- 插入知识点数据，使用当前数据库中存在的course_id

-- 插入计算机专业概论(课程ID:12)的知识点
INSERT INTO knowledge_point (kp_name, course_id, difficulty, description) VALUES 
('计算机发展简史', 12, '简单', '计算机的起源和发展历程'),
('计算机系统组成', 12, '中等', '计算机硬件和软件系统的组成'),
('计算机网络基础', 12, '中等', '计算机网络的基本概念和原理');

-- 插入子知识点
INSERT INTO knowledge_point (kp_name, course_id, parent_kp_id, difficulty, description) VALUES 
('硬件系统', 12, 1, '简单', '计算机硬件的组成部分'),
('软件系统', 12, 1, '简单', '计算机软件的分类和功能'),
('局域网', 12, 3, '中等', '局域网的工作原理和应用'),
('广域网', 12, 3, '中等', '广域网的工作原理和应用');

-- 插入电工及电子技术(课程ID:13)的知识点
INSERT INTO knowledge_point (kp_name, course_id, difficulty, description) VALUES 
('电路基础', 13, '简单', '电路的基本概念和定律'),
('电子元件', 13, '中等', '常用电子元件的特性和应用');

-- 插入算法设计与分析(课程ID:14)的知识点
INSERT INTO knowledge_point (kp_name, course_id, difficulty, description) VALUES 
('算法基础', 14, '简单', '算法的基本概念和复杂度分析'),
('排序算法', 14, '中等', '各种排序算法的原理和实现'),
('搜索算法', 14, '中等', '各种搜索算法的原理和实现');

-- 插入编译原理(课程ID:15)的知识点
INSERT INTO knowledge_point (kp_name, course_id, difficulty, description) VALUES 
('编译原理概述', 15, '简单', '编译原理的基本概念和编译过程'),
('词法分析', 15, '中等', '词法分析的原理和实现');

-- 插入数值分析(课程ID:16)的知识点
INSERT INTO knowledge_point (kp_name, course_id, difficulty, description) VALUES 
('数值分析基础', 16, '简单', '数值分析的基本概念和误差分析'),
('线性方程组求解', 16, '中等', '线性方程组的数值求解方法');

-- 插入完成提示
SELECT '知识点数据插入完成！' AS message;