# 校园新闻系统 - 项目全貌

> 📌 这份文档是给"未来的我"准备的。如果你刚接手这个项目，读完这份就能直接干活。

## 一、项目定位

前后端分离的校园新闻管理系统。用户可以浏览新闻、评论；管理员可以发布/编辑/下架新闻、管理分类、审核评论、管理用户。

## 二、技术栈

| 层 | 技术 | 说明 |
|---|------|------|
| 后端框架 | Spring Boot 3.2.5 | Java 17 |
| ORM | MyBatis-Plus 3.5.6 | 基于 MyBatis 增强，自动生成 CRUD |
| 数据库 | MySQL 8.0+ | utf8mb4 编码 |
| 认证 | JWT (jjwt 0.12.5) | 无状态 Token 认证 |
| 接口文档 | Knife4j 4.4.0 | Swagger 增强，访问 `/doc.html` |
| 前端框架 | Vue 3.4 | Composition API + `<script setup>` |
| 构建工具 | Vite 5 | 开发服务器带代理 |
| UI 库 | Element Plus 2.7 | 中文组件库 |
| 状态管理 | Pinia | 替代 Vuex |
| HTTP | Axios | 封装了拦截器 |

## 三、项目结构

```
workspace/
├── campus-news-backend/          # 后端
│   ├── pom.xml
│   ├── sql/schema.sql            # ⚠️ 必须先执行这个建库建表
│   ├── README.md                 # 后端说明文档
│   └── src/main/
│       ├── java/com/campusnews/
│       │   ├── CampusNewsApplication.java      # 启动类
│       │   ├── common/                          # 公共组件
│       │   │   ├── Result.java                  # 统一响应格式 {code, message, data}
│       │   │   ├── PageResult.java              # 分页结果 {records, total, current, size, pages}
│       │   │   ├── JwtUtil.java                 # JWT 生成/解析/验证
│       │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│       │   ├── config/
│       │   │   ├── WebMvcConfig.java            # 拦截器注册 + 跨域 + 静态资源映射
│       │   │   ├── MybatisPlusConfig.java       # 分页插件 + 自动填充 createTime/updateTime
│       │   │   └── OpenApiConfig.java           # Swagger 文档配置
│       │   ├── interceptor/
│       │   │   ├── AuthInterceptor.java         # JWT 校验，解析后把 userId/username/role 写入 request
│       │   │   └── AdminInterceptor.java        # 管理员权限校验（检查 role === ADMIN）
│       │   ├── entity/                          # 实体类（对应数据库表）
│       │   │   ├── User.java                    # sys_user 表
│       │   │   ├── Category.java                # news_category 表
│       │   │   ├── News.java                    # news_article 表
│       │   │   └── Comment.java                 # news_comment 表
│       │   ├── dto/                             # 请求参数对象
│       │   │   ├── LoginDTO.java                # username + password
│       │   │   ├── UserDTO.java                 # 注册/编辑用户
│       │   │   ├── NewsDTO.java                 # 创建/编辑新闻
│       │   │   └── CommentDTO.java              # 发表评论
│       │   ├── mapper/                          # MyBatis-Plus Mapper（基本是空接口，继承 BaseMapper）
│       │   ├── service/                         # Service 接口 + impl/ 实现类
│       │   └── controller/
│       │       ├── AuthController.java          # /api/auth - 登录/注册/获取当前用户
│       │       ├── NewsController.java          # /api/news - 前台：新闻列表/详情/置顶/点赞
│       │       ├── AdminNewsController.java     # /api/admin/news - 后台：新闻CRUD/发布/置顶
│       │       ├── CategoryController.java      # /api/category + /api/admin/category
│       │       ├── CommentController.java       # /api/comments + /api/admin/comments
│       │       ├── AdminUserController.java     # /api/admin/user - 用户管理
│       │       └── FileController.java          # /api/file/upload - 文件上传，按日期存到 uploads/
│       └── resources/
│           ├── application.yml                  # 主配置（端口、JWT密钥、MyBatis配置）
│           └── application-dev.yml              # 数据库连接配置
│
└── campus-news-frontend/         # 前端
    ├── package.json
    ├── vite.config.js            # 开发代理：/api → localhost:8080
    ├── index.html
    └── src/
        ├── main.js               # 入口，注册 Element Plus + Pinia + Router
        ├── App.vue
        ├── api/                  # 每个模块一个文件，封装 HTTP 请求
        │   ├── auth.js           # login, register, getUserInfo
        │   ├── news.js           # getNewsList, getNewsDetail, getTopNews, likeNews
        │   ├── category.js       # getCategories + 后台 CRUD
        │   ├── comment.js        # getComments, addComment + 后台审核
        │   └── admin.js          # 后台：新闻/用户管理 + 文件上传
        ├── utils/
        │   └── request.js        # Axios 实例：自动带 Bearer Token，401 自动跳登录
        ├── stores/
        │   └── user.js           # Pinia store：token, userInfo, isLoggedIn, isAdmin
        ├── router/
        │   └── index.js          # 路由 + beforeEach 守卫（检查登录+管理员权限）
        └── views/
            ├── front/            # 前台页面
            │   ├── Layout.vue    # 顶栏 + router-view + 底栏
            │   ├── Login.vue     # 登录表单
            │   ├── Register.vue  # 注册表单（带确认密码校验）
            │   ├── Home.vue      # 轮播置顶 + 分类筛选 + 分页新闻列表
            │   └── NewsDetail.vue # 详情 + 点赞 + 评论列表 + 发表评论
            └── admin/            # 后台管理（需 ADMIN 权限）
                ├── AdminLayout.vue    # 侧边栏菜单布局
                ├── NewsManage.vue     # 新闻列表（发布/下架/置顶/删除）
                ├── NewsEdit.vue       # 新闻编辑器（封面上传 + 富文本HTML）
                ├── CategoryManage.vue # 分类增删改
                ├── CommentManage.vue  # 评论审核（通过/拒绝/删除）
                └── UserManage.vue     # 用户列表（编辑角色/禁用启用）
```

## 四、数据库设计

### 4 张核心表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `sys_user` | 用户表 | username(PK), password(BCrypt), role(ADMIN/EDITOR/USER), status |
| `news_category` | 分类表 | name, sortOrder, status |
| `news_article` | 新闻表 | title, content(LONGTEXT), categoryId(FK), authorId(FK), status(0草稿/1发布/2下架), isTop, viewCount, likeCount |
| `news_comment` | 评论表 | newsId(FK), userId(FK), content, parentId(回复), status(0待审核/1通过/2拒绝) |

所有表都有 `is_deleted` 逻辑删除字段，MyBatis-Plus 自动处理。

### 默认数据
- 管理员账号：`admin` / `admin123`
- 预设分类：校园动态、通知公告、学术科研、文体活动、就业实习

## 五、认证流程

```
前端                          后端
 │                              │
 │  POST /api/auth/login        │
 │  {username, password}  ────► │  BCrypt 校验密码
 │                              │  生成 JWT (24h过期)
 │  ◄──── {token, userInfo}     │
 │                              │
 │  GET /api/admin/news/list    │
 │  Authorization: Bearer xxx   │
 │                        ────► │  AuthInterceptor 解析 JWT
 │                              │  把 userId/role 写入 request
 │                              │  AdminInterceptor 检查 role=ADMIN
 │  ◄──── {data}                │
```

**关键点：**
- Token 存在 `localStorage`，Axios 拦截器自动附加
- 401 响应 → 自动清除 token → 跳转登录页
- 后端通过拦截器链：AuthInterceptor → Controller → AdminInterceptor（仅 /api/admin/*）

## 六、API 路由一览

### 公开接口（无需 Token）
- `POST /api/auth/login` - 登录
- `POST /api/auth/register` - 注册
- `GET /api/news/list` - 新闻列表（支持 categoryId, keyword, page, size）
- `GET /api/news/detail/{id}` - 新闻详情（自动 +1 浏览量）
- `GET /api/news/top` - 置顶新闻
- `GET /api/category/list` - 启用的分类
- `GET /api/comments/list/{newsId}` - 已通过的评论
- `POST /api/file/upload` - 上传文件

### 需登录（Token）
- `GET /api/auth/info` - 当前用户信息
- `POST /api/comments/add` - 发表评论
- `POST /api/news/like/{id}` - 点赞

### 需管理员（Token + ADMIN）
- `/api/admin/news/*` - 新闻管理
- `/api/admin/category/*` - 分类管理
- `/api/admin/comments/*` - 评论审核
- `/api/admin/user/*` - 用户管理

## 七、启动方式

### 后端
```bash
# 1. 建库建表
mysql -u root -p < campus-news-backend/sql/schema.sql

# 2. 修改数据库配置
# 编辑 campus-news-backend/src/main/resources/application-dev.yml

# 3. 启动
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
# 前端运行在 localhost:5173，自动代理 /api 到 8080
```

## 八、开发约定

### 后端
- **统一响应格式**：所有接口返回 `Result<T>`，`{code: 200, message: "操作成功", data: ...}`
- **分页**：MyBatis-Plus 分页插件，返回 `PageResult<T>`
- **自动填充**：`createTime` 和 `updateTime` 通过 `MetaObjectHandler` 自动处理，实体类加 `@TableField(fill = FieldFill.INSERT)`
- **逻辑删除**：`isDeleted` 字段，`@TableLogic` 注解
- **密码加密**：BCrypt，`BCryptPasswordEncoder`

### 前端
- **Composition API**：全部用 `<script setup>` 语法
- **API 封装**：每个模块一个文件，统一用 `request.js` 的 Axios 实例
- **状态管理**：用户信息在 Pinia store，同步到 localStorage
- **路由守卫**：`meta.requiresAuth` 控制登录，`meta.requiresAdmin` 控制管理员

## 九、已知的简化/待优化项

以下功能当前是基础实现，后续可增强：

1. **富文本编辑器** — NewsEdit.vue 目前用的是 textarea + HTML 手写，可替换为 TinyMCE / Quill / WangEditor
2. **密码 hash** — schema.sql 中的 admin 密码 hash 是用 Python bcrypt 生成的（`$2a$` 前缀），Spring 的 BCryptPasswordEncoder 能正常校验
3. **文件上传** — 存本地 `./uploads/` 目录，生产环境建议改 OSS
4. **评论** — 目前只支持一级回复，没有嵌套楼中楼
5. **搜索** — 标题模糊搜索，没有全文搜索
6. **用户头像** — 没有默认头像，上传后才显示
7. **没有单元测试**
8. **前端样式** — 基础 Element Plus 样式，没有自定义主题
9. **新闻编辑** — 封面上传后没有预览大图，详情页获取用的是列表接口的筛选方式（不够优雅，应直接 getById）

## 十、Git 仓库

- 地址：`https://github.com/142093601/yb`
- 分支：`master`
- 前后端在同一个仓库，分两个文件夹
