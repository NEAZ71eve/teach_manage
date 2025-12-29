# 题库管理系统数据库设计

## 1. 设计目标

- 支持多种题型管理（单选题、多选题、判断题、填空题、简答题）
- 实现题目分类和知识点关联
- 支持题目标签和统计分析
- 支持批量操作和导入导出
- 提供灵活的检索和筛选功能

## 2. 数据库表结构设计

### 2.1 题目分类表（question_category）

| 字段名 | 数据类型 | 长度 | 约束 | 描述 |
|-------|---------|------|------|------|
| category_id | INT | 11 | PRIMARY KEY, AUTO_INCREMENT | 分类ID |
| category_name | VARCHAR | 100 | NOT NULL | 分类名称 |
| description | TEXT | | | 分类描述 |
| parent_category_id | INT | 11 | FOREIGN KEY | 父分类ID，用于树形结构 |
| category_order | INT | 11 | NOT NULL DEFAULT 0 | 分类排序 |
| is_active | TINYINT | 1 | NOT NULL DEFAULT 1 | 是否激活（1-激活，0-禁用） |
| create_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.2 知识点表（knowledge_point）

| 字段名 | 数据类型 | 长度 | 约束 | 描述 |
|-------|---------|------|------|------|
| point_id | INT | 11 | PRIMARY KEY, AUTO_INCREMENT | 知识点ID |
| point_name | VARCHAR | 100 | NOT NULL | 知识点名称 |
| course_id | INT | 11 | FOREIGN KEY | 所属课程ID |
| parent_id | INT | 11 | FOREIGN KEY | 父知识点ID，用于树形结构 |
| description | TEXT | | | 知识点描述 |
| difficulty | VARCHAR | 20 | | 知识点难度（简单、中等、困难） |
| kp_order | INT | 11 | NOT NULL DEFAULT 0 | 知识点排序 |
| is_active | TINYINT | 1 | NOT NULL DEFAULT 1 | 是否激活（1-激活，0-禁用） |
| create_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.3 题目表（question）

| 字段名 | 数据类型 | 长度 | 约束 | 描述 |
|-------|---------|------|------|------|
| question_id | INT | 11 | PRIMARY KEY, AUTO_INCREMENT | 题目ID |
| question_type | TINYINT | 1 | NOT NULL | 题目类型（1-单选题，2-多选题，3-判断题，4-填空题，5-简答题） |
| question_content | TEXT | | NOT NULL | 题目内容 |
| category_id | INT | 11 | FOREIGN KEY | 所属分类ID |
| point_id | INT | 11 | FOREIGN KEY | 所属知识点ID |
| difficulty | VARCHAR | 20 | NOT NULL | 题目难度（简单、中等、困难） |
| score | DOUBLE | | NOT NULL | 题目分数 |
| correct_answer | TEXT | | NOT NULL | 正确答案 |
| analysis | TEXT | | | 答案解析 |
| is_used | TINYINT | 1 | NOT NULL DEFAULT 0 | 是否被使用（0-未使用，1-已使用） |
| status | VARCHAR | 20 | NOT NULL DEFAULT 'draft' | 题目状态（draft-草稿，review-审核中，published-已发布，rejected-已拒绝） |
| creator_id | INT | 11 | FOREIGN KEY | 创建者ID |
| reviewer_id | INT | 11 | FOREIGN KEY | 审核者ID |
| review_time | DATETIME | | | 审核时间 |
| review_remark | TEXT | | | 审核备注 |
| create_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.4 题目选项表（question_option）

| 字段名 | 数据类型 | 长度 | 约束 | 描述 |
|-------|---------|------|------|------|
| option_id | INT | 11 | PRIMARY KEY, AUTO_INCREMENT | 选项ID |
| question_id | INT | 11 | FOREIGN KEY | 所属题目ID |
| option_text | TEXT | | NOT NULL | 选项内容 |
| option_order | INT | 11 | NOT NULL | 选项顺序 |
| is_correct | TINYINT | 1 | NOT NULL DEFAULT 0 | 是否正确选项（0-错误，1-正确） |
| create_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.5 题目标签表（question_tag）

| 字段名 | 数据类型 | 长度 | 约束 | 描述 |
|-------|---------|------|------|------|
| tag_id | INT | 11 | PRIMARY KEY, AUTO_INCREMENT | 标签ID |
| tag_name | VARCHAR | 50 | NOT NULL UNIQUE | 标签名称 |
| tag_description | TEXT | | | 标签描述 |
| is_active | TINYINT | 1 | NOT NULL DEFAULT 1 | 是否激活（1-激活，0-禁用） |
| create_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.6 题目-标签关联表（question_tag_relation）

| 字段名 | 数据类型 | 长度 | 约束 | 描述 |
|-------|---------|------|------|------|
| relation_id | INT | 11 | PRIMARY KEY, AUTO_INCREMENT | 关联ID |
| question_id | INT | 11 | FOREIGN KEY | 题目ID |
| tag_id | INT | 11 | FOREIGN KEY | 标签ID |
| create_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 2.7 题目统计表（question_statistics）

| 字段名 | 数据类型 | 长度 | 约束 | 描述 |
|-------|---------|------|------|------|
| stat_id | INT | 11 | PRIMARY KEY, AUTO_INCREMENT | 统计ID |
| question_id | INT | 11 | FOREIGN KEY | 题目ID |
| usage_count | INT | 11 | NOT NULL DEFAULT 0 | 使用次数 |
| correct_count | INT | 11 | NOT NULL DEFAULT 0 | 答对次数 |
| incorrect_count | INT | 11 | NOT NULL DEFAULT 0 | 答错次数 |
| correct_rate | DOUBLE | | NOT NULL DEFAULT 0 | 正确率 |
| last_used_time | DATETIME | | | 最后使用时间 |
| create_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.8 题目导入导出日志表（question_import_export_log）

| 字段名 | 数据类型 | 长度 | 约束 | 描述 |
|-------|---------|------|------|------|
| log_id | INT | 11 | PRIMARY KEY, AUTO_INCREMENT | 日志ID |
| operation_type | VARCHAR | 20 | NOT NULL | 操作类型（import-导入，export-导出） |
| file_name | VARCHAR | 255 | NOT NULL | 文件名 |
| file_path | VARCHAR | 500 | NOT NULL | 文件路径 |
| record_count | INT | 11 | NOT NULL | 记录数量 |
| success_count | INT | 11 | NOT NULL | 成功数量 |
| failure_count | INT | 11 | NOT NULL | 失败数量 |
| operator_id | INT | 11 | FOREIGN KEY | 操作人ID |
| operation_time | DATETIME | | NOT NULL DEFAULT CURRENT_TIMESTAMP | 操作时间 |
| remark | TEXT | | | 备注 |

## 3. 实体类设计

### 3.1 QuestionCategory.java

```java
@Data
public class QuestionCategory {
    private Integer categoryId;
    private String categoryName;
    private String description;
    private Integer parentCategoryId;
    private Integer categoryOrder;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private List<QuestionCategory> children;
    private String parentCategoryName;
}
```

### 3.2 KnowledgePoint.java

```java
@Data
public class KnowledgePoint {
    private Integer pointId;
    private String pointName;
    private Integer courseId;
    private Integer parentId;
    private String description;
    private String difficulty;
    private Integer kpOrder;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private List<KnowledgePoint> children;
    private String courseName;
    private String parentName;
}
```

### 3.3 Question.java

```java
@Data
public class Question {
    private Integer questionId;
    private Integer questionType;
    private String questionContent;
    private Integer categoryId;
    private Integer pointId;
    private String difficulty;
    private Double score;
    private String correctAnswer;
    private String analysis;
    private Integer isUsed;
    private String status;
    private Integer creatorId;
    private Integer reviewerId;
    private LocalDateTime reviewTime;
    private String reviewRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String categoryName;
    private String pointName;
    private List<QuestionOption> options;
    private List<QuestionTag> tags;
    private QuestionStatistics statistics;
}
```

### 3.4 QuestionOption.java

```java
@Data
public class QuestionOption {
    private Integer optionId;
    private Integer questionId;
    private String optionText;
    private Integer optionOrder;
    private Integer isCorrect;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### 3.5 QuestionTag.java

```java
@Data
public class QuestionTag {
    private Integer tagId;
    private String tagName;
    private String tagDescription;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### 3.6 QuestionTagRelation.java

```java
@Data
public class QuestionTagRelation {
    private Integer relationId;
    private Integer questionId;
    private Integer tagId;
    private LocalDateTime createTime;
}
```

### 3.7 QuestionStatistics.java

```java
@Data
public class QuestionStatistics {
    private Integer statId;
    private Integer questionId;
    private Integer usageCount;
    private Integer correctCount;
    private Integer incorrectCount;
    private Double correctRate;
    private LocalDateTime lastUsedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### 3.8 QuestionImportExportLog.java

```java
@Data
public class QuestionImportExportLog {
    private Integer logId;
    private String operationType;
    private String fileName;
    private String filePath;
    private Integer recordCount;
    private Integer successCount;
    private Integer failureCount;
    private Integer operatorId;
    private LocalDateTime operationTime;
    private String remark;
}
```

## 4. 功能模块设计

### 4.1 题目管理

- 题目添加：支持多种题型的题目添加，包括选项管理
- 题目编辑：支持题目信息的修改，包括选项调整
- 题目删除：支持单个和批量删除题目
- 题目审核：支持题目审核流程，包括审核通过、拒绝等
- 题目状态管理：支持草稿、审核中、已发布、已拒绝等状态切换

### 4.2 题目分类管理

- 分类添加：支持添加一级和二级分类
- 分类编辑：支持修改分类信息
- 分类删除：支持删除分类，同时处理关联题目
- 分类树形展示：支持分类的树形结构展示

### 4.3 知识点管理

- 知识点添加：支持添加一级和二级知识点
- 知识点编辑：支持修改知识点信息
- 知识点删除：支持删除知识点，同时处理关联题目
- 知识点树形展示：支持知识点的树形结构展示

### 4.4 标签管理

- 标签添加：支持添加题目标签
- 标签编辑：支持修改标签信息
- 标签删除：支持删除标签，同时处理关联关系
- 标签关联：支持给题目添加和移除标签

### 4.5 题目统计分析

- 题目使用统计：统计题目被使用的次数
- 正确率统计：统计题目被作答的正确率
- 难度分析：根据正确率分析题目实际难度
- 知识点覆盖率分析：分析知识点的题目覆盖情况

### 4.6 导入导出功能

- 题目导入：支持从Excel、Word等格式导入题目
- 题目导出：支持将题目导出为Excel、Word、PDF等格式
- 导入导出日志：记录导入导出操作日志

### 4.7 检索筛选功能

- 多条件检索：支持按题型、难度、分类、知识点、标签等条件检索题目
- 模糊搜索：支持根据题目内容进行模糊搜索
- 高级筛选：支持组合条件筛选，如难度范围、分数范围等

## 5. 数据库优化建议

1. **索引优化**：
   - 为经常用于检索的字段创建索引，如question_type、difficulty、category_id、point_id等
   - 为外键字段创建索引，提高关联查询性能
   - 为status和is_active字段创建索引，提高状态筛选性能

2. **分表分库考虑**：
   - 当题目数量达到百万级别时，考虑按知识点或分类进行分表
   - 当系统用户量较大时，考虑按用户或机构进行分库

3. **缓存优化**：
   - 对分类、知识点、标签等静态数据进行缓存
   - 对热点题目数据进行缓存，提高查询性能
   - 对统计数据进行定期计算和缓存，减少实时计算压力

4. **数据备份和恢复**：
   - 定期对数据库进行全量备份
   - 对重要操作（如批量删除、导入导出）进行日志记录，便于数据恢复
   - 实现数据恢复机制，支持按时间点恢复数据

## 6. 系统安全建议

1. **数据访问控制**：
   - 实现基于角色的访问控制（RBAC），限制不同角色对题库的操作权限
   - 对敏感操作（如删除题目、批量导入）进行日志记录和审计

2. **数据加密**：
   - 对用户密码进行加密存储
   - 对敏感数据（如题目答案）进行加密传输

3. **防止SQL注入**：
   - 使用参数化查询，防止SQL注入攻击
   - 对用户输入进行严格验证和过滤

4. **防止XSS攻击**：
   - 对用户输入的HTML内容进行过滤和转义
   - 设置适当的HTTP头，防止XSS攻击

5. **防止CSRF攻击**：
   - 实现CSRF令牌机制，防止跨站请求伪造攻击
   - 对重要操作（如修改题目、删除题目）进行二次确认

## 7. 总结

本设计方案提供了一个全面的题库管理系统数据库设计，支持多种题型管理、分类和知识点关联、标签管理、统计分析、导入导出等功能。通过合理的数据库表结构设计和优化建议，可以提高系统的性能、安全性和可扩展性，满足不同规模和需求的题库管理需求。