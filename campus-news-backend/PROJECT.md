# 校园新闻系统 - 项目全貌

> 📌 这份文档是给"未来的我"准备的。如果你刚接手这个项目，读完这份就能直接干活。

## 一、项目定位

前后端分离的校园新闻管理系统。用户可以浏览新闻、评论；管理员可以发布/编辑/下架新闻、管理分类、审核评论、管理用户。

## 二、技术栈

| 层 | 技术 | 版本要求 |
|---|------|----------|
| Java | JDK | **17+**（Spring Boot 3.x 硬性要求，Java 11 不行） |
| 后端框架 | Spring Boot | 3.2.5 |
| ORM | MyBatis-Plus | 3.5.6 |
| 数据库 | MySQL | 8.0+，utf8mb4 编码 |
| 认证 | JWT (jjwt) | 0.12.5 |
| 密码加密 | spring-security-crypto | BCrypt（只引入 crypto 模块，不引入 Spring Security 全套） |
| 接口文档 | Knife4j | 4.4.0，访问 `/doc.html` |
| 前端框架 | Vue 3 | 3.4+，Composition API + `<script setup>` |
| 构建工具 | Vite | 5.x，开发服务器带代理 |
| UI 库 | Element Plus | 2.7，中文组件库 |
| 状态管理 | Pinia | 替代 Vuex |
| HTTP | Axios | 封装了拦截器 |

## 三、项目结构

```
workspace/（Git 仓库根目录）
├── .gitignore
├── campus-news-backend/          # 后端（Spring Boot）
│   ├── pom.xml                   # Maven 依赖
│   ├── README.md                 # 后端说明文档
│   ├── PROJECT.md                # ← 你现在在看的这份文档
│   ├── sql/schema.sql            # ⚠️ 必须先执行这个建库建表
│   └── src/main/
│       ├── java/com/campusnews/
│       │   ├── CampusNewsApplication.java      # 启动类（@MapperScan 扫描 mapper 包）
│       │   │
│       │   ├── common/                          # 公共组件
│       │   │   ├── Result.java                  # 统一响应 {code, message, data}
│       │   │   ├── PageResult.java              # 分页结果 {records, total, current, size, pages}
│       │   │   ├── JwtUtil.java                 # JWT 生成/解析/验证，密钥在 application.yml
│       │   │   └── GlobalExceptionHandler.java  # 全局异常处理（参数校验、非法参数、兜底异常）
│       │   │
│       │   ├── config/
│       │   │   ├── WebMvcConfig.java            # 三件事：
│       │   │   │                                  # 1. 注册 AuthInterceptor 和 AdminInterceptor
│       │   │   │                                  # 2. 跨域配置（CorsFilter）
│       │   │   │                                  # 3. 静态资源映射（/uploads/** → file:./uploads/）
│       │   │   ├── MybatisPlusConfig.java       # 分页插件 + MetaObjectHandler 自动填充 createTime/updateTime
│       │   │   └── OpenApiConfig.java           # Knife4j Swagger 文档配置
│       │   │
│       │   ├── interceptor/
│       │   │   ├── AuthInterceptor.java         # JWT 校验拦截器
│       │   │   │                                  # 流程：取 Authorization header → 去 Bearer 前缀 → 验证 Token
│       │   │   │                                  # → 把 userId/username/role 写入 request attributes
│       │   │   │                                  # → 放行 OPTIONS 预检请求
│       │   │   └── AdminInterceptor.java        # 管理员权限拦截器，检查 role === "ADMIN"
│       │   │
│       │   ├── entity/                          # 实体类（@TableName 对应数据库表）
│       │   │   ├── User.java                    # sys_user：username, password(BCrypt), role(ADMIN/EDITOR/USER)
│       │   │   ├── Category.java                # news_category：name, sortOrder, status
│       │   │   ├── News.java                    # news_article：title, content(LONGTEXT), categoryId, status(0/1/2)
│       │   │   │                                  # 非DB字段 categoryName 用 @TableField(exist=false) 填充
│       │   │   └── Comment.java                 # news_comment：newsId, userId, content, parentId, status(0/1/2)
│       │   │
│       │   ├── dto/                             # 请求参数对象（带 @Valid 校验注解）
│       │   │   ├── LoginDTO.java                # username + password
│       │   │   ├── UserDTO.java                 # 注册/编辑用户
│       │   │   ├── NewsDTO.java                 # 创建/编辑新闻
│       │   │   └── CommentDTO.java              # 发表评论
│       │   │
│       │   ├── mapper/                          # MyBatis-Plus Mapper 接口
│       │   │   ├── UserMapper.java              # extends BaseMapper<User>，基本是空接口
│       │   │   ├── CategoryMapper.java
│       │   │   ├── NewsMapper.java
│       │   │   └── CommentMapper.java
│       │   │
│       │   ├── service/                         # Service 接口层
│       │   │   ├── UserService.java
│       │   │   ├── CategoryService.java
│       │   │   ├── NewsService.java
│       │   │   ├── CommentService.java
│       │   │   └── impl/                        # Service 实现类
│       │   │       ├── UserServiceImpl.java     # 核心：登录校验 BCrypt、注册时加密密码、生成 JWT
│       │   │       ├── CategoryServiceImpl.java # 删除分类前检查是否有新闻引用
│       │   │       ├── NewsServiceImpl.java     # 批量填充分类名称、浏览量+1、点赞
│       │   │       └── CommentServiceImpl.java  # 评论默认 status=0（待审核）
│       │   │
│       │   └── controller/
│       │       ├── AuthController.java          # /api/auth          登录/注册/当前用户
│       │       ├── NewsController.java          # /api/news          前台：列表/详情/置顶/点赞
│       │       ├── AdminNewsController.java     # /api/admin/news    后台：CRUD/发布/置顶
│       │       ├── CategoryController.java      # /api/category      分类（前台+后台合并一个Controller）
│       │       ├── CommentController.java       # /api/comments      评论（前台+后台合并一个Controller）
│       │       ├── AdminUserController.java     # /api/admin/user    用户管理
│       │       └── FileController.java          # /api/file/upload   文件上传，按日期存储
│       │
│       └── resources/
│           ├── application.yml                  # 主配置：端口8080、JWT密钥/过期时间、MyBatis配置、文件上传路径
│           └── application-dev.yml              # 数据库连接配置（这个要改成本地的）
│
└── campus-news-frontend/         # 前端（Vue 3 + Vite）
    ├── package.json              # 依赖：vue, vue-router, pinia, axios, element-plus
    ├── vite.config.js            # Vite 配置，开发代理 /api → localhost:8080
    ├── index.html
    ├── .gitignore
    └── src/
        ├── main.js               # 入口：注册 Element Plus（中文locale）+ Pinia + Router + 全部图标
        ├── App.vue               # 就一个 <router-view />
        │
        ├── api/                  # 接口层，每个模块一个文件
        │   ├── auth.js           # login, register, getUserInfo
        │   ├── news.js           # getNewsList, getNewsDetail, getTopNews, likeNews
        │   ├── category.js       # 前台 getCategories + 后台 CRUD
        │   ├── comment.js        # 前台 getComments/addComment + 后台 audit/delete
        │   └── admin.js          # 后台：新闻/用户管理 + 文件上传（uploadFile 封装了 FormData）
        │
        ├── utils/
        │   └── request.js        # Axios 实例：
        │                           # - baseURL: '/api'
        │                           # - 请求拦截器：自动加 Bearer Token
        │                           # - 响应拦截器：code!=200 弹错误提示；401 清Token跳登录
        │
        ├── stores/
        │   └── user.js           # Pinia Store：
        │                           # - token, userInfo（同步到 localStorage）
        │                           # - isLoggedIn, isAdmin, isEditor, canManage 计算属性
        │                           # - setLogin(data), logout()
        │
        ├── router/
        │   └── index.js          # 路由定义 + beforeEach 守卫
        │                           # - meta.requiresAuth → 检查 token
        │                           # - meta.requiresAdmin → 检查 role=ADMIN
        │
        └── views/
            ├── front/            # 前台页面
            │   ├── Layout.vue    # 顶栏（logo + 登录/用户下拉菜单）+ <router-view> + 底栏
            │   ├── Login.vue     # 登录表单，成功后根据角色跳转（ADMIN→后台，其他→首页）
            │   ├── Register.vue  # 注册表单，带确认密码校验
            │   ├── Home.vue      # 首页：
            │   │                   # - 顶部轮播：el-carousel 显示置顶新闻
            │   │                   # - 分类筛选：el-radio-group
            │   │                   # - 搜索框
            │   │                   # - 新闻卡片列表（封面+标题+摘要+分类标签+浏览量）
            │   │                   # - 底部分页
            │   └── NewsDetail.vue # 详情页：
            │                       # - 标题+元信息（分类/作者/时间/浏览/点赞）
            │                       # - 富文本内容（v-html）
            │                       # - 点赞按钮
            │                       # - 评论区（登录可发表，未登录提示）
            │
            └── admin/            # 后台管理（需 ADMIN 权限）
                ├── AdminLayout.vue    # 左侧菜单（新闻/分类/评论/用户）+ 右侧内容区
                ├── NewsManage.vue     # 新闻列表表格：发布/下架、置顶、编辑、删除
                ├── NewsEdit.vue       # 新闻编辑：标题、分类选择、封面上传、摘要、内容（textarea HTML）
                ├── CategoryManage.vue # 分类增删改（弹窗表单）
                ├── CommentManage.vue  # 评论审核：筛选状态、通过/拒绝、删除
                └── UserManage.vue     # 用户管理：搜索、编辑角色、禁用/启用
```

## 四、数据库设计

### 4 张核心表

**sys_user（用户表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT, AUTO_INCREMENT | 主键 |
| username | VARCHAR(50), UNIQUE | 用户名 |
| password | VARCHAR(100) | BCrypt 加密后的密码 |
| nickname | VARCHAR(50) | 昵称 |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| avatar | VARCHAR(255) | 头像URL |
| role | VARCHAR(20) | ADMIN / EDITOR / USER |
| status | TINYINT | 0禁用 / 1启用 |
| is_deleted | TINYINT | 逻辑删除 |

**news_category（分类表）**
| 字段 | 说明 |
|------|------|
| id | 主键 |
| name | 分类名称 |
| sortOrder | 排序号 |
| status | 0禁用 / 1启用 |

**news_article（新闻表）**
| 字段 | 说明 |
|------|------|
| id | 主键 |
| title | 标题 |
| summary | 摘要 |
| coverImage | 封面图URL |
| content | LONGTEXT 富文本正文 |
| categoryId | 分类ID（FK） |
| authorId | 作者ID（FK） |
| authorName | 作者名（冗余） |
| viewCount | 浏览量 |
| likeCount | 点赞数 |
| status | 0草稿 / 1已发布 / 2已下架 |
| isTop | 0否 / 1是 |
| publishTime | 发布时间 |

**news_comment（评论表）**
| 字段 | 说明 |
|------|------|
| id | 主键 |
| newsId | 新闻ID（FK） |
| userId | 用户ID（FK） |
| nickname | 用户昵称（冗余） |
| avatar | 用户头像（冗余） |
| content | 评论内容 |
| parentId | 父评论ID（回复时用） |
| replyNickname | 被回复人的昵称 |
| status | 0待审核 / 1已通过 / 2已拒绝 |

### 默认数据
- 管理员：`admin` / `admin123`（BCrypt 加密存储）
- 预设分类：校园动态、通知公告、学术科研、文体活动、就业实习

## 五、认证流程

```
前端                                    后端
 │                                        │
 │  POST /api/auth/login                  │
 │  {username, password}            ───►  │  UserServiceImpl.login()
 │                                        │    → 查用户（LambdaQueryWrapper）
 │                                        │    → BCryptPasswordEncoder.matches() 校验密码
 │                                        │    → JwtUtil.generateToken(userId, username, role)
 │  ◄──  {code:200, data:{token, ...}}    │
 │                                        │
 │  后续请求都带 Header:                   │
 │  Authorization: Bearer <token>         │
 │                                        │
 │  GET /api/admin/news/list        ───►  │  WebMvcConfig 拦截器链：
 │                                        │    1. AuthInterceptor.preHandle()
 │                                        │       → 解析 JWT → request.setAttribute("role", ...)
 │                                        │    2. → 进入 Controller
 │                                        │    3. AdminInterceptor（仅 /api/admin/*）
 │                                        │       → 检查 role == "ADMIN"
 │                                        │       → 不是则返回 403
 │  ◄──  {data}                           │
```

**Token 细节：**
- 密钥：`application.yml` 中的 `jwt.secret`
- 过期时间：86400000ms（24小时）
- Claims：subject=userId, 额外携带 username 和 role
- 前端存储：localStorage，Axios 拦截器自动读取并加到 Header

## 六、API 路由一览

### 公开接口（无需 Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 登录 |
| POST | /api/auth/register | 注册 |
| GET | /api/news/list | 新闻列表（params: categoryId, keyword, page, size） |
| GET | /api/news/detail/{id} | 新闻详情（自动 +1 浏览量） |
| GET | /api/news/top | 置顶新闻（最多5条） |
| GET | /api/category/list | 启用的分类 |
| GET | /api/comments/list/{newsId} | 已通过的评论 |
| POST | /api/file/upload | 上传文件（multipart） |

### 需登录（Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/auth/info | 当前用户信息 |
| POST | /api/comments/add | 发表评论（默认待审核） |
| POST | /api/news/like/{id} | 点赞 |

### 需管理员（Token + ADMIN）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/news/list | 后台新闻列表 |
| POST | /api/admin/news/save | 创建/编辑新闻 |
| DELETE | /api/admin/news/delete/{id} | 删除新闻 |
| PUT | /api/admin/news/publish/{id}?status= | 发布(1)/下架(2) |
| PUT | /api/admin/news/top/{id} | 置顶/取消置顶 |
| GET | /api/admin/user/list | 用户列表 |
| PUT | /api/admin/user/update | 编辑用户 |
| PUT | /api/admin/user/toggle/{id} | 禁用/启用 |
| GET | /api/admin/category/list | 所有分类 |
| POST | /api/admin/category/add | 添加分类 |
| PUT | /api/admin/category/update | 更新分类 |
| DELETE | /api/admin/category/delete/{id} | 删除分类 |
| GET | /api/admin/comments/list | 所有评论 |
| PUT | /api/admin/comments/audit/{id}?status= | 审核(1通过/2拒绝) |
| DELETE | /api/admin/comments/delete/{id} | 删除评论 |

## 七、启动方式

### 后端
```bash
# 1. 确认 Java 版本 ≥ 17
java -version

# 2. 建库建表
mysql -u root -p < campus-news-backend/sql/schema.sql

# 3. 修改数据库配置
# 编辑 campus-news-backend/src/main/resources/application-dev.yml
# 改成你本地的 MySQL 地址、用户名、密码

# 4. 启动
cd campus-news-backend
mvn spring-boot:run
# 后端运行在 localhost:8080
# 接口文档：http://localhost:8080/doc.html
```

### 前端
```bash
cd campus-news-frontend
npm install
npm run dev
# 前端运行在 localhost:5173
# 自动代理 /api 到 localhost:8080
```

### 登录测试
- 管理员：`admin` / `admin123`
- 登录后可以进入 `/admin` 后台管理

## 八、开发约定

### 后端
- **统一响应**：所有接口返回 `Result<T>`，`{code: 200, message: "操作成功", data: ...}`
- **分页**：MyBatis-Plus 分页插件，返回 `PageResult<T>`
- **自动填充**：`createTime`/`updateTime` 通过 `MetaObjectHandler`，实体类加 `@TableField(fill = FieldFill.INSERT)`
- **逻辑删除**：`@TableLogic` 注解，删除操作实际是更新 `is_deleted=1`
- **密码**：BCrypt 加密，用 `spring-security-crypto`（不是完整 Spring Security）
- **跨域**：`CorsFilter` Bean，放行所有源

### 前端
- **语法**：全部 `<script setup>` + Composition API
- **API**：每个模块一个文件，统一用 `request.js` 的 Axios 实例
- **状态**：用户信息在 Pinia store，同步到 localStorage
- **路由守卫**：`meta.requiresAuth` / `meta.requiresAdmin`

## 九、已知的简化项与待优化

1. **富文本编辑器** — NewsEdit 用的是 textarea 手写 HTML，可换 Quill/WangEditor/TinyMCE
2. **文件存储** — 本地 uploads 目录，生产建议改 OSS
3. **评论** — 只支持一级回复，无嵌套楼中楼
4. **搜索** — 标题 like 模糊搜索，无全文检索
5. **默认头像** — 未上传头像时显示空白
6. **无单元测试**
7. **前端样式** — 基础 Element Plus，无自定义主题
8. **详情页获取** — 编辑时用列表接口筛选，应直接 getById

## 十、已修复的问题

1. ~~BCryptPasswordEncoder 找不到~~ → 添加了 `spring-security-crypto` 依赖
2. ~~application-dev.yml 未提交~~ → 从 .gitignore 中移除并补充提交

## 十一、Git 仓库

- 地址：`https://github.com/142093601/yb`
- 分支：`master`
- 前后端在同一仓库，分 `campus-news-backend/` 和 `campus-news-frontend/` 两个文件夹
