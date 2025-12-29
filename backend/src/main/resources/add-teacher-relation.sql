-- 修改培养方案表，添加负责老师字段
ALTER TABLE training_program ADD COLUMN teacher_id INT COMMENT '负责老师ID';
ALTER TABLE training_program ADD FOREIGN KEY (teacher_id) REFERENCES `user`(user_id) ON DELETE SET NULL ON UPDATE CASCADE;

-- 修改课程表，添加课程组老师字段
ALTER TABLE course ADD COLUMN teacher_ids VARCHAR(255) COMMENT '课程组老师ID列表，逗号分隔';
