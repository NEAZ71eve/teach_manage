# 本科专业管理系统 - Spring Boot + Vue

## 项目简介

本项目是一个基于Spring Boot + Vue的本科专业管理系统，主要用于管理课程、知识点、实践项目等教学资源。

## 技术栈

### 后端
- Spring Boot 3.2.x
- Spring Data JPA
- MySQL 9.5
- MyBatis-Plus (可选)
- Lombok

### 前端
- Vue 3
- Element Plus
- Axios
- Vue Router
- Pinia

## 项目结构

```
spring-boot-vue-course-management/
├── backend/                 # 后端Spring Boot项目
│   ├── src/main/java/
│   │   └── com/
│   │       └── example/
│   │           └── coursemanagement/
│   │               ├── CourseManagementApplication.java  # 主应用类
│   │               ├── config/                          # 配置类
│   │               ├── controller/                      # 控制器
│   │               ├── entity/                          # 实体类
│   │               ├── mapper/                          # MyBatis映射器
│   │               ├── repository/                      # JPA仓库
│   │               ├── service/                         # 服务层
│   │               └── vo/                              # 视图对象
│   ├── src/main/resources/
│   │   ├── application.yml                              # 应用配置
│   │   └── mybatis/
│   │       └── mapper/                                  # MyBatis映射文件
│   └── pom.xml                                          # Maven依赖
├── frontend/                # 前端Vue项目
│   ├── public/
│   ├── src/
│   │   ├── assets/                                     # 静态资源
│   │   ├── components/                                 # Vue组件
│   │   ├── router/                                     # 路由配置
│   │   ├── store/                                      # Pinia状态管理
│   │   ├── views/                                      # 页面组件
│   │   ├── api/                                        # API请求
│   │   ├── utils/                                      # 工具函数
│   │   ├── App.vue                                     # 根组件
│   │   └── main.js                                     # 入口文件
│   ├── index.html
│   ├── package.json                                    # npm依赖
│   └── vite.config.js                                   # Vite配置
└── README.md                                           # 项目说明
```

## 核心功能

1. **课程管理**
   - 课程列表查询（带分页）
   - 课程添加、编辑、删除
   - 课程详情查看
   - 课程按类型统计

2. **知识点管理**
   - 知识点树状结构展示
   - 知识点添加、编辑、删除
   - 知识点按课程分类

3. **实践项目管理**
   - 实践项目列表
   - 实践项目详情
   - 项目按学期分类

4. **学期管理**
   - 学期安排查看
   - 教学计划展示

## 快速开始

### 后端启动

1. 确保MySQL数据库已启动，创建数据库`major_management`
2. 修改`backend/src/main/resources/application.yml`中的数据库连接配置
3. 运行`CourseManagementApplication.java`主类
4. 后端服务将在`http://localhost:8080`启动

### 前端启动

1. 进入frontend目录
2. 安装依赖：`npm install`
3. 启动开发服务器：`npm run dev`
4. 访问`http://localhost:5173`

## 数据库设计

### 主要表结构

- `major` - 专业表
- `course` - 课程表
- `knowledge_point` - 知识点表
- `practice_project` - 实践项目表
- `semester_schedule` - 学期教学安排表
- `credit_distribution` - 学分学时分配表

## API接口

### 课程管理API

| 接口路径 | 方法 | 描述 |
|---------|------|------|
| `/api/courses` | GET | 获取课程列表（带分页） |
| `/api/courses/{id}` | GET | 获取课程详情 |
| `/api/courses` | POST | 添加课程 |
| `/api/courses/{id}` | PUT | 更新课程 |
| `/api/courses/{id}` | DELETE | 删除课程 |
| `/api/courses/stats` | GET | 获取课程统计信息 |

### 知识点管理API

| 接口路径 | 方法 | 描述 |
|---------|------|------|
| `/api/knowledge-points` | GET | 获取知识点列表 |
| `/api/knowledge-points/tree/{courseId}` | GET | 获取知识点树结构 |
| `/api/knowledge-points` | POST | 添加知识点 |
| `/api/knowledge-points/{id}` | PUT | 更新知识点 |
| `/api/knowledge-points/{id}` | DELETE | 删除知识点 |

### 实践项目API

| 接口路径 | 方法 | 描述 |
|---------|------|------|
| `/api/practice-projects` | GET | 获取实践项目列表 |
| `/api/practice-projects/{id}` | GET | 获取实践项目详情 |

## 开发说明

1. 后端使用Spring Boot 3.2.x，支持JPA和MyBatis两种持久化方式
2. 前端使用Vue 3 + Element Plus构建响应式界面
3. 前后端分离架构，通过RESTful API通信
4. 支持跨域请求

## 构建生产版本

### 后端构建

```bash
cd backend
mvn clean package -DskipTests
```

### 前端构建

```bash
cd frontend
npm run build
```

## 部署说明

1. 将后端打包生成的jar文件部署到服务器
2. 将前端打包生成的dist目录部署到Nginx或其他Web服务器
3. 配置Nginx反向代理，将API请求转发到后端服务

## 许可证

MIT License
