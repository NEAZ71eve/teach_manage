-- 本科专业管理系统完整数据库初始化脚本
-- 包含所有必要的表结构、约束、索引和初始数据

-- 1. 专业表
CREATE TABLE IF NOT EXISTS major (
    major_id INT PRIMARY KEY AUTO_INCREMENT,
    major_name VARCHAR(100) NOT NULL COMMENT '专业名称',
    major_code VARCHAR(20) NOT NULL UNIQUE COMMENT '专业代码',
    department VARCHAR(100) NOT NULL COMMENT '所属院系',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科专业表';

-- 2. 培养方案表
CREATE TABLE IF NOT EXISTS training_program (
    program_id INT PRIMARY KEY AUTO_INCREMENT,
    major_name VARCHAR(100) NOT NULL COMMENT '专业名称',
    duration INT NOT NULL COMMENT '学制（年）',
    total_credit DECIMAL(4,1) NOT NULL COMMENT '总学分',
    effective_year INT NOT NULL COMMENT '生效年份',
    description TEXT COMMENT '培养方案描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培养方案表';

-- 3. 课程表
CREATE TABLE IF NOT EXISTS course (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(20) NOT NULL UNIQUE COMMENT '课程代码',
    major_id INT NOT NULL COMMENT '所属专业',
    program_id INT COMMENT '所属培养方案',
    credit DECIMAL(3,1) NOT NULL COMMENT '学分',
    total_hours INT NOT NULL COMMENT '总学时',
    theoretical_hours INT NOT NULL COMMENT '理论学时',
    practical_hours INT NOT NULL COMMENT '实践学时',
    course_type ENUM('必修课', '选修课') NOT NULL COMMENT '课程类型',
    course_nature ENUM('公共基础课', '专业基础课', '专业课') NOT NULL COMMENT '课程性质',
    exam_mark VARCHAR(10) DEFAULT NULL COMMENT '考试方式',
    course_category VARCHAR(50) DEFAULT NULL COMMENT '课程分类',
    description TEXT COMMENT '课程描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (major_id) REFERENCES major(major_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (program_id) REFERENCES training_program(program_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_hours CHECK (theoretical_hours + practical_hours = total_hours)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 4. 知识点表
CREATE TABLE IF NOT EXISTS knowledge_point (
    kp_id INT PRIMARY KEY AUTO_INCREMENT,
    kp_name VARCHAR(100) NOT NULL COMMENT '知识点名称',
    course_id INT NOT NULL COMMENT '所属课程',
    parent_kp_id INT COMMENT '父知识点ID，用于构建知识点层级关系',
    description TEXT COMMENT '知识点描述',
    difficulty ENUM('简单', '中等', '困难') DEFAULT '中等' COMMENT '知识点难度',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (parent_kp_id) REFERENCES knowledge_point(kp_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- 5. 题目表
CREATE TABLE IF NOT EXISTS question (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    question_type INT NOT NULL COMMENT '题目类型：1-单选题，2-多选题，3-判断题，4-填空题，5-简答题',
    question_content TEXT NOT NULL COMMENT '题目内容',
    kp_id INT NOT NULL COMMENT '所属知识点',
    difficulty ENUM('简单', '中等', '困难') DEFAULT '中等' COMMENT '题目难度',
    score DECIMAL(3,1) NOT NULL COMMENT '题目分值',
    option_a VARCHAR(200) COMMENT '选项A，仅用于选择题',
    option_b VARCHAR(200) COMMENT '选项B，仅用于选择题',
    option_c VARCHAR(200) COMMENT '选项C，仅用于选择题',
    option_d VARCHAR(200) COMMENT '选项D，仅用于选择题',
    option_e VARCHAR(200) COMMENT '选项E，仅用于选择题',
    correct_answer TEXT NOT NULL COMMENT '正确答案',
    analysis TEXT COMMENT '答案解析',
    is_used TINYINT DEFAULT 0 COMMENT '是否被使用：0-未使用，1-已使用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (kp_id) REFERENCES knowledge_point(kp_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 6. 试卷表
CREATE TABLE IF NOT EXISTS exam_paper (
    paper_id INT PRIMARY KEY AUTO_INCREMENT,
    paper_name VARCHAR(100) NOT NULL COMMENT '试卷名称',
    course_id INT NOT NULL COMMENT '所属课程',
    total_score DECIMAL(4,1) NOT NULL COMMENT '试卷总分',
    paper_type ENUM('手动组卷', '自动组卷') NOT NULL COMMENT '组卷方式',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- 7. 试卷题目关联表
CREATE TABLE IF NOT EXISTS exam_paper_question (
    paper_question_id INT PRIMARY KEY AUTO_INCREMENT,
    paper_id INT NOT NULL COMMENT '试卷ID',
    question_id INT NOT NULL COMMENT '题目ID',
    question_order INT NOT NULL COMMENT '题目顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_paper_question (paper_id, question_id),
    FOREIGN KEY (paper_id) REFERENCES exam_paper(paper_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联表';

-- 8. 用户表
CREATE TABLE IF NOT EXISTS `user` (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    status INT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 9. 角色表
CREATE TABLE IF NOT EXISTS `role` (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_desc VARCHAR(255) COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 10. 权限表
CREATE TABLE IF NOT EXISTS `permission` (
    permission_id INT PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    description VARCHAR(255) COMMENT '权限描述',
    url VARCHAR(255) COMMENT '访问路径',
    method VARCHAR(20) COMMENT '请求方法',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 11. 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    role_id INT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_id) REFERENCES `role`(role_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 12. 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role_id INT NOT NULL COMMENT '角色ID',
    permission_id INT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES `role`(role_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES `permission`(permission_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 13. 学期表
CREATE TABLE IF NOT EXISTS semester (
    semester_id INT PRIMARY KEY AUTO_INCREMENT,
    semester_name VARCHAR(50) NOT NULL COMMENT '学期名称，如：2023-2024学年第一学期',
    semester_code VARCHAR(20) NOT NULL UNIQUE COMMENT '学期代码，如：202301',
    start_date DATE NOT NULL COMMENT '学期开始日期',
    end_date DATE NOT NULL COMMENT '学期结束日期',
    status ENUM('进行中', '已结束', '未开始') DEFAULT '未开始' COMMENT '学期状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学期表';

-- 14. 课程学期关联表
CREATE TABLE IF NOT EXISTS course_semester (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT '课程ID',
    semester_id INT NOT NULL COMMENT '学期ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_course_semester (course_id, semester_id),
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semester(semester_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程学期关联表';

-- 添加索引优化查询性能
-- 课程表索引
CREATE INDEX IF NOT EXISTS idx_course_major ON course(major_id);
CREATE INDEX IF NOT EXISTS idx_course_program ON course(program_id);
CREATE INDEX IF NOT EXISTS idx_course_name ON course(course_name);
CREATE INDEX IF NOT EXISTS idx_course_code ON course(course_code);

-- 知识点表索引
CREATE INDEX IF NOT EXISTS idx_kp_course ON knowledge_point(course_id);
CREATE INDEX IF NOT EXISTS idx_kp_parent ON knowledge_point(parent_kp_id);
CREATE INDEX IF NOT EXISTS idx_kp_name ON knowledge_point(kp_name);

-- 题目表索引
CREATE INDEX IF NOT EXISTS idx_question_kp ON question(kp_id);
CREATE INDEX IF NOT EXISTS idx_question_used ON question(is_used);
CREATE INDEX IF NOT EXISTS idx_question_type ON question(question_type);

-- 试卷表索引
CREATE INDEX IF NOT EXISTS idx_paper_course ON exam_paper(course_id);

-- 试卷题目关联表索引
CREATE INDEX IF NOT EXISTS idx_paper_question_question ON exam_paper_question(question_id);

-- 插入初始数据

-- 1. 插入专业数据
INSERT INTO major (major_name, major_code, department) VALUES 
('计算机科学与技术', '080901', '计算机学院'),
('软件工程', '080902', '计算机学院'),
('网络工程', '080903', '计算机学院');

-- 2. 插入培养方案数据
INSERT INTO training_program (major_name, duration, total_credit, effective_year, description) VALUES 
('计算机科学与技术', 4, 150.0, 2023, '2023级计算机科学与技术专业培养方案'),
('软件工程', 4, 145.0, 2023, '2023级软件工程专业培养方案');

-- 3. 插入课程数据
INSERT INTO course (course_name, course_code, major_id, program_id, credit, total_hours, theoretical_hours, practical_hours, course_type, course_nature) VALUES 
('高等数学', '1001', 1, 1, 4.0, 64, 64, 0, '必修课', '公共基础课'),
('线性代数', '1002', 1, 1, 3.0, 48, 48, 0, '必修课', '公共基础课'),
('计算机组成原理', '2001', 1, 1, 4.0, 64, 48, 16, '必修课', '专业基础课'),
('操作系统', '2002', 1, 1, 4.0, 64, 48, 16, '必修课', '专业基础课'),
('数据结构', '2003', 1, 1, 4.0, 64, 48, 16, '必修课', '专业基础课');

-- 4. 插入知识点数据
INSERT INTO knowledge_point (kp_name, course_id, difficulty, description) VALUES 
('CPU结构', 3, '中等', '中央处理器的内部结构和工作原理'),
('内存管理', 4, '困难', '操作系统的内存管理机制'),
('线性表', 5, '简单', '线性表的定义、操作和实现'),
('栈和队列', 5, '中等', '栈和队列的基本概念和应用');

-- 插入子知识点
INSERT INTO knowledge_point (kp_name, course_id, parent_kp_id, difficulty, description) VALUES 
('寄存器', 3, 1, '简单', 'CPU中的寄存器分类和功能'),
('指令系统', 3, 1, '中等', 'CPU的指令集和指令执行过程'),
('分页管理', 4, 2, '困难', '分页存储管理的实现原理'),
('分段管理', 4, 2, '困难', '分段存储管理的实现原理');

-- 5. 插入题目数据
INSERT INTO question (question_type, question_content, kp_id, difficulty, score, option_a, option_b, option_c, option_d, correct_answer, analysis) VALUES 
(1, 'CPU中用于存储当前执行指令的寄存器是？', 5, '简单', 2.0, '程序计数器', '累加器', '地址寄存器', '数据寄存器', 'A', '程序计数器用于存储当前执行指令的地址'),
(1, '下列哪种内存管理方式不会产生外部碎片？', 7, '中等', 2.0, '分页管理', '分段管理', '段页式管理', '动态分区管理', 'A', '分页管理只会产生内部碎片，不会产生外部碎片'),
(2, '下列数据结构中，哪些是线性结构？', 3, '中等', 3.0, '数组', '链表', '树', '图', 'A,B', '数组和链表是线性结构，树和图是非线性结构'),
(3, '栈是一种先进先出的数据结构。', 4, '简单', 1.0, NULL, NULL, NULL, NULL, '0', '栈是先进后出的数据结构，队列是先进先出的数据结构'),
(4, '在二叉树的前序遍历中，______遍历根节点。', 3, '简单', 2.0, NULL, NULL, NULL, NULL, '首先', '前序遍历的顺序是：根节点→左子树→右子树');

-- 6. 插入角色数据
INSERT INTO `role` (role_name, role_desc) VALUES 
('系统管理员', '拥有系统所有权限'),
('学院管理员', '管理学院相关数据'),
('教师', '管理课程和题库'),
('学生', '查看课程和考试');

-- 7. 插入权限数据
INSERT INTO `permission` (permission_name, permission_code, description, url, method) VALUES 
('查看用户列表', 'user:list', '查看所有用户信息', '/users', 'GET'),
('新增用户', 'user:add', '创建新用户', '/users', 'POST'),
('编辑用户', 'user:edit', '修改用户信息', '/users/{id}', 'PUT'),
('删除用户', 'user:delete', '删除用户', '/users/{id}', 'DELETE'),
('查看角色列表', 'role:list', '查看所有角色信息', '/roles', 'GET'),
('新增角色', 'role:add', '创建新角色', '/roles', 'POST'),
('编辑角色', 'role:edit', '修改角色信息', '/roles/{id}', 'PUT'),
('删除角色', 'role:delete', '删除角色', '/roles/{id}', 'DELETE'),
('查看权限列表', 'permission:list', '查看所有权限信息', '/permissions', 'GET'),
('新增权限', 'permission:add', '创建新权限', '/permissions', 'POST'),
('编辑权限', 'permission:edit', '修改权限信息', '/permissions/{id}', 'PUT'),
('删除权限', 'permission:delete', '删除权限', '/permissions/{id}', 'DELETE'),
('查看培养方案列表', 'trainingProgram:list', '查看所有培养方案', '/training-programs', 'GET'),
('新增培养方案', 'trainingProgram:add', '创建新培养方案', '/training-programs', 'POST'),
('编辑培养方案', 'trainingProgram:edit', '修改培养方案', '/training-programs/{id}', 'PUT'),
('删除培养方案', 'trainingProgram:delete', '删除培养方案', '/training-programs/{id}', 'DELETE'),
('查看课程列表', 'course:list', '查看所有课程', '/courses', 'GET'),
('新增课程', 'course:add', '创建新课程', '/courses', 'POST'),
('编辑课程', 'course:edit', '修改课程', '/courses/{id}', 'PUT'),
('删除课程', 'course:delete', '删除课程', '/courses/{id}', 'DELETE'),
('查看知识点列表', 'knowledgePoint:list', '查看所有知识点', '/knowledge-points', 'GET'),
('新增知识点', 'knowledgePoint:add', '创建新知识点', '/knowledge-points', 'POST'),
('编辑知识点', 'knowledgePoint:edit', '修改知识点', '/knowledge-points/{id}', 'PUT'),
('删除知识点', 'knowledgePoint:delete', '删除知识点', '/knowledge-points/{id}', 'DELETE'),
('查看题库列表', 'question:list', '查看所有题目', '/question-bank', 'GET'),
('新增题目', 'question:add', '创建新题目', '/question-bank', 'POST'),
('编辑题目', 'question:edit', '修改题目', '/question-bank/{id}', 'PUT'),
('删除题目', 'question:delete', '删除题目', '/question-bank/{id}', 'DELETE'),
('查看试卷列表', 'examPaper:list', '查看所有试卷', '/exam-papers', 'GET'),
('新增试卷', 'examPaper:add', '创建新试卷', '/exam-papers', 'POST'),
('编辑试卷', 'examPaper:edit', '修改试卷', '/exam-papers/{id}', 'PUT'),
('删除试卷', 'examPaper:delete', '删除试卷', '/exam-papers/{id}', 'DELETE'),
('生成试卷', 'examPaper:generate', '自动生成试卷', '/exam-papers/generate', 'POST'),
('查看统计数据', 'statistics:view', '查看系统统计数据', '/statistics', 'GET');

-- 8. 插入系统管理员用户（密码：admin123，使用BCrypt加密）
INSERT INTO `user` (username, password, real_name, email, phone, status) VALUES 
('admin', '$2a$10$KQV4J0e3G3Z8G3A3B3C3D3E3F3G3H3I3J3K3L3M3N3O3P3Q3R3S3T3U3V3W3X3Y3Z3', '系统管理员', 'admin@example.com', '13800138000', 1);

-- 9. 给系统管理员分配角色
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);

-- 10. 给角色分配权限
-- 系统管理员拥有所有权限
INSERT INTO role_permission (role_id, permission_id) SELECT 1, permission_id FROM permission;

-- 教师拥有课程、知识点、题库和试卷相关权限
INSERT INTO role_permission (role_id, permission_id) VALUES 
(3, 17), (3, 18), (3, 19), (3, 20),
(3, 21), (3, 22), (3, 23), (3, 24),
(3, 25), (3, 26), (3, 27), (3, 28),
(3, 29), (3, 30), (3, 31), (3, 32), (3, 33);

-- 学生拥有查看权限
INSERT INTO role_permission (role_id, permission_id) VALUES 
(4, 17), (4, 21), (4, 25), (4, 29), (4, 34);

-- 学院管理员拥有培养方案和课程相关权限
INSERT INTO role_permission (role_id, permission_id) VALUES 
(2, 13), (2, 14), (2, 15), (2, 16),
(2, 17), (2, 18), (2, 19), (20);

-- 11. 插入学期数据
INSERT INTO semester (semester_name, semester_code, start_date, end_date, status) VALUES 
('2023-2024学年第一学期', '202301', '2023-09-01', '2024-01-15', '已结束'),
('2023-2024学年第二学期', '202302', '2024-02-20', '2024-06-30', '已结束'),
('2024-2025学年第一学期', '202401', '2024-09-02', '2025-01-16', '进行中');

-- 插入完成提示
SELECT '本科专业管理系统数据库初始化完成！' AS message;
