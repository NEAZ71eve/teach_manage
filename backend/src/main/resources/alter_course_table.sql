ALTER TABLE course ADD COLUMN program_id INT COMMENT '所属培养方案';
ALTER TABLE course ADD FOREIGN KEY (program_id) REFERENCES training_program(program_id) ON DELETE SET NULL ON UPDATE CASCADE;