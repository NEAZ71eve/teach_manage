-- 本科专业管理系统数据库初始化脚本

-- 1. 培养方案表
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

-- 2. 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `real_name` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `status` INT DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 角色表
CREATE TABLE IF NOT EXISTS `role` (
  `role_id` INT AUTO_INCREMENT PRIMARY KEY,
  `role_name` VARCHAR(50) NOT NULL UNIQUE,
  `role_desc` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 权限表
CREATE TABLE IF NOT EXISTS `permission` (
  `permission_id` INT AUTO_INCREMENT PRIMARY KEY,
  `permission_name` VARCHAR(100) NOT NULL,
  `permission_code` VARCHAR(100) NOT NULL UNIQUE,
  `description` VARCHAR(255),
  `url` VARCHAR(255),
  `method` VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `role_id` INT NOT NULL,
  `permission_id` INT NOT NULL,
  FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`) ON DELETE CASCADE,
  FOREIGN KEY (`permission_id`) REFERENCES `permission`(`permission_id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 修改course表，添加program_id字段关联培养方案
ALTER TABLE course ADD COLUMN program_id INT COMMENT '所属培养方案';
ALTER TABLE course ADD FOREIGN KEY (program_id) REFERENCES training_program(program_id) ON DELETE SET NULL ON UPDATE CASCADE;

-- 8. 学期表
CREATE TABLE IF NOT EXISTS semester (
    semester_id INT PRIMARY KEY AUTO_INCREMENT,
    semester_name VARCHAR(50) NOT NULL COMMENT '学期名称，如：2024-2025学年第一学期',
    start_date DATE NOT NULL COMMENT '学期开始日期',
    end_date DATE NOT NULL COMMENT '学期结束日期',
    is_current BOOLEAN DEFAULT FALSE COMMENT '是否当前学期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学期表';

-- 9. 课程表
CREATE TABLE IF NOT EXISTS course_schedule (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT '课程ID',
    semester_id INT NOT NULL COMMENT '学期ID',
    week_day INT NOT NULL COMMENT '星期几（1-7，周一到周日）',
    class_section INT NOT NULL COMMENT '节次（1-12）',
    start_time VARCHAR(10) NOT NULL COMMENT '开始时间，如：08:00',
    end_time VARCHAR(10) NOT NULL COMMENT '结束时间，如：08:45',
    classroom VARCHAR(50) NOT NULL COMMENT '教室',
    teacher VARCHAR(20) NOT NULL COMMENT '教师',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semester(semester_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 插入初始角色数据
INSERT INTO `role` (`role_name`, `role_desc`) VALUES 
('系统管理员', '拥有系统所有权限'),
('学院管理员', '管理学院相关数据'),
('教师', '管理课程和题库'),
('学生', '查看课程和考试');

-- 插入初始权限数据
INSERT INTO `permission` (`permission_name`, `permission_code`, `description`, `url`, `method`) VALUES 
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
('删除权限', 'permission:delete', '删除权限', '/permissions/{id}', 'DELETE');

-- 插入初始用户数据（密码：123456，使用BCrypt加密）
INSERT INTO `user` (`username`, `password`, `real_name`, `email`, `phone`, `status`) VALUES 
('admin', '$2a$10$e5e6b3f4a2d1c0b9a8e7d6c5b4a3d2c1b0a9e8d7c6b5a4d3c2b1a0', '系统管理员', 'admin@example.com', '13800138000', 1);
