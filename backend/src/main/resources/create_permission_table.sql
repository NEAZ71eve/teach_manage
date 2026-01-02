CREATE TABLE IF NOT EXISTS `permission` (
  `permission_id` INT AUTO_INCREMENT PRIMARY KEY,
  `permission_name` VARCHAR(100) NOT NULL,
  `permission_code` VARCHAR(100) NOT NULL UNIQUE,
  `description` VARCHAR(255),
  `url` VARCHAR(255),
  `method` VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;