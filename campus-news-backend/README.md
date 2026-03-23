# 校园新闻系统 - 后端

基于 Spring Boot 3.2.5 + MyBatis-Plus 的校园新闻管理系统后端。

## 技术栈

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.2.5 |
| MyBatis-Plus | 3.5.6 |
| MySQL | 8.0+ |
| JWT (jjwt) | 0.12.5 |
| Knife4j (Swagger) | 4.4.0 |
| Java | 17+ |

## 项目结构

```
campus-news-backend/
├── pom.xml
├── sql/
│   └── schema.sql              # 数据库建表脚本
└── src/main/java/com/campusnews/
    ├── CampusNewsApplication.java   # 启动类
    ├── common/                      # 公共类
    │   ├── Result.java              # 统一响应
    │   ├── PageResult.java          # 分页结果
    │   ├── JwtUtil.java             # JWT工具
    │   └── GlobalExceptionHandler.java
    ├── config/                      # 配置类
    │   ├── WebMvcConfig.java        # 拦截器+跨域
    │   ├── MybatisPlusConfig.java   # 分页+自动填充
    │   └── OpenApiConfig.java       # Swagger文档
    ├── interceptor/                 # 拦截器
    │   ├── AuthInterceptor.java     # 登录认证
    │   └── AdminInterceptor.java    # 管理员权限
    ├── entity/                      # 实体类
    │   ├── User.java
    │   ├── Category.java
    │   ├── News.java
    │   └── Comment.java
    ├── dto/                         # 请求DTO
    ├── mapper/                      # Mapper接口
    ├── service/                     # Service层
    │   └── impl/
    └── controller/                  # 控制器
        ├── AuthController.java      # 登录注册
        ├── NewsController.java      # 前台新闻
        ├── CategoryController.java  # 分类
        ├── CommentController.java   # 评论
        ├── AdminNewsController.java # 后台新闻
        ├── AdminUserController.java # 后台用户
        └── FileController.java      # 文件上传
```

## 快速开始

### 1. 准备数据库

```bash
mysql -u root -p < sql/schema.sql
```

### 2. 修改配置

编辑 `src/main/resources/application-dev.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_news?...
    username: root
    password: your_password
```

### 3. 启动项目

```bash
mvn spring-boot:run
```

### 4. 访问接口文档

http://localhost:8080/doc.html

## API 一览

### 认证 (`/api/auth`)
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /login | 登录 |
| POST | /register | 注册 |
| GET  | /info | 当前用户信息 |

### 前台新闻 (`/api/news`)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /list | 新闻列表 |
| GET  | /detail/{id} | 新闻详情 |
| GET  | /top | 置顶新闻 |
| POST | /like/{id} | 点赞 |

### 分类 (`/api/category`)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /list | 分类列表 |

### 评论 (`/api/comments`)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /list/{newsId} | 评论列表 |
| POST | /add | 添加评论 |

### 后台管理 (`/api/admin`)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /news/list | 新闻管理列表 |
| POST | /news/save | 保存新闻 |
| DELETE | /news/delete/{id} | 删除新闻 |
| PUT  | /news/publish/{id} | 发布/下架 |
| PUT  | /news/top/{id} | 置顶/取消 |
| GET  | /user/list | 用户列表 |
| PUT  | /user/toggle/{id} | 禁用/启用 |
| GET  | /category/list | 分类列表 |
| POST | /category/add | 添加分类 |
| PUT  | /category/update | 更新分类 |
| DELETE | /category/delete/{id} | 删除分类 |
| GET  | /comments/list | 评论列表 |
| PUT  | /comments/audit/{id} | 审核评论 |

## 默认账号

- 用户名：`admin`
- 密码：`admin123`
