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
│       │   │   ├── CommentDTO.java              # 发表评论
│       │   │   ├── UpdateProfileDTO.java        # 🆕 修改个人信息（nickname/email/phone/avatar）
│       │   │   └── ChangePasswordDTO.java       # 🆕 修改密码（oldPassword + newPassword，带校验）
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
│       │       ├── AuthController.java          # /api/auth          登录/注册/当前用户/修改信息/改密码
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
├── campus-news-backend/
│   └── sql/
│       ├── schema.sql            # 建库建表脚本
│       └── seed_data.sql         # 🆕 种子数据（15篇新闻 + 20条评论 + 4个测试用户）
│
└── campus-news-frontend/         # 前端（Vue 3 + Vite）
    ├── package.json              # 依赖：vue, vue-router, pinia, axios, element-plus
    ├── vite.config.js            # Vite 配置，开发代理 /api → localhost:8080
    ├── index.html
    ├── .gitignore
    └── src/
        ├── main.js               # 入口：注册 Element Plus（中文locale）+ Pinia + Router + 全部图标
        ├── App.vue               # 全局样式重置 + 自定义滚动条 + 统一主色调(#4f6ef7)
        │
        ├── api/                  # 接口层，每个模块一个文件
        │   ├── auth.js           # login, register, getUserInfo, updateProfile, changePassword
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
        │                           # - setLogin(data), updateUserInfo(data), logout()
        │
        ├── router/
        │   └── index.js          # 路由定义 + beforeEach 守卫
        │                           # - meta.requiresAuth → 检查 token（含 /profile）
        │                           # - meta.requiresAdmin → 检查 role=ADMIN
        │
        └── views/
            ├── front/            # 前台页面（v2/v3 持续美化）
            │   ├── Layout.vue    # 毛玻璃顶栏 + Logo品牌区 + 导航高亮
            │   │                   # - 导航：首页 / 搜索 / 归档
            │   │                   # - 用户头像 + 下拉菜单（角色标签 + 个人中心入口）
            │   │                   # - 注册按钮（渐变色）
            │   │                   # - 丰富 Footer（品牌/快速导航/分类链接/版权）
            │   ├── Login.vue     # 左右分栏布局：
            │   │                   # - 左：品牌展示区（蓝紫渐变）+ 功能亮点
            │   │                   # - 右：登录表单（圆角按钮 + 图标输入框）
            │   │                   # - 移动端自动隐藏左侧面板
            │   ├── Register.vue  # 同样左右分栏，青绿渐变 + 注册福利展示
            │   ├── Profile.vue   # 个人中心：
            │   │                   # - 顶部渐变头像卡片（头像 + 昵称 + 角色标签）
            │   │                   # - 基本信息展示（用户名/昵称/邮箱/手机号/角色）
            │   │                   # - 编辑资料表单（昵称/邮箱/手机号/头像URL）
            │   │                   # - 修改密码弹窗（旧密码验证 + 新密码确认）
            │   │                   # - 修改后同步更新 Pinia store + localStorage
            │   ├── Search.vue     # 🆕 搜索页：
            │   │                   # - 顶部搜索框（大尺寸，带阴影，回车/按钮触发搜索）
            │   │                   # - 搜索结果：关键词高亮、封面图+标签+摘要+元信息
            │   │                   # - 未搜索时展示热点排行（按浏览量排序TOP15，排名徽章）
            │   │                   # - 分页支持
            │   ├── Archive.vue    # 🆕 归档页：
            │   │                   # - 按月份分组的时间线布局
            │   │                   # - 左侧时间轴圆点 + 右侧新闻卡片（封面+标签+摘要）
            │   │                   # - 分类筛选按钮
            │   │                   # - 加载更多
            │   ├── Home.vue      # 🆕 v3 改版：
            │   │                   # - Hero 统计区（渐变背景 + 新闻数/分类数/用户数）
            │   │                   # - 轮播图（el-carousel 展示置顶热点，4秒切换，封面背景 + 渐变遮罩）
            │   │                   # - 三栏布局：左分类导航 / 中新闻列表（横向卡片）/ 右热门排行+公告
            │   │                   # - 分类高亮切换，搜索框
            │   │                   # - 响应式：≤1200px 隐藏左栏，≤768px 单列堆叠
            │   └── NewsDetail.vue # 改进：
            │                       # - 面包屑导航（首页 > 分类 > 正文）
            │                       # - 左右布局：正文 + 侧边文章信息卡
            │                       # - 优雅点赞按钮（hover动效 + 计数器）
            │                       # - 头像化评论区 + 居中登录提示
            │
            └── admin/            # 后台管理（需 ADMIN 权限，本次未改动）
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

### 默认数据（schema.sql 自带）
- 管理员：`admin` / `admin123`（BCrypt 加密存储）
- 预设分类：校园动态、通知公告、学术科研、文体活动、就业实习

### 测试数据（seed_data.sql，需手动导入）
执行 `seed_data.sql` 后额外获得：
- **15 篇新闻文章**：每个分类 2-3 篇，含标题、摘要、完整 HTML 正文、浏览量、点赞数
- **20 条评论**：分布于 7 篇新闻下，状态均为"已通过"
- **4 位测试用户**（密码均为 `admin123`）：

| 用户名 | 昵称 | 角色 |
|--------|------|------|
| zhangsan | 小张同学 | USER |
| lisi | 李学姐 | USER |
| wangwu | 王五不姓王 | USER |
| xiaoming | 明明同学 | USER |

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
| PUT | /api/auth/profile | 🆕 修改个人信息（nickname/email/phone/avatar） |
| PUT | /api/auth/password | 🆕 修改密码（需旧密码验证，新密码6-20位） |
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

# 3. 导入种子数据（可选，导入后有测试内容可看）
mysql -u root -p < campus-news-backend/sql/seed_data.sql

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
- 管理员：`admin` / `admin123`（可进入 `/admin` 后台管理）
- 普通用户：`zhangsan` / `admin123`（仅前台浏览/评论/点赞）

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
5. ~~默认头像~~ — ✅ v3 个人中心已支持头像设置，未设置时显示首字母占位
6. **无单元测试**
7. **详情页获取** — 编辑时用列表接口筛选，应直接 getById
8. ~~前端样式~~ — ✅ v2 已美化，但仍有提升空间（可加暗色模式、动画等）

## 十、更新日志

### v4 — 2026-03-24 搜索页 + 归档页 + 导航栏扩展

#### 新增页面
- **搜索页（`Search.vue`）**：访问 `/search`
  - 顶部大搜索框（带阴影、回车/按钮触发搜索）
  - 搜索结果列表：关键词 `<mark>` 高亮显示，封面图+标签+摘要+元信息
  - 未搜索时展示**热点排行**：按浏览量排序 TOP15，排名徽章（前3名金色渐变）
  - 分页支持
- **归档页（`Archive.vue`）**：访问 `/archive`
  - 按月份分组的时间线布局（左侧圆点连线 + 右侧新闻卡片）
  - 月份标题显示文章数量
  - 分类筛选按钮（pill 样式）
  - 加载更多按钮

#### 导航栏扩展
- **Layout.vue**：顶栏导航从「首页」扩展为「首页 / 搜索 / 归档」
- 每个导航项带对应图标（HomeFilled / Search / Collection）

#### 路由
- `router/index.js`：新增 `/search` 和 `/archive` 路由（无需登录）

#### 设计要点
- 搜索页灵感来自微博热搜：搜索框 + 热点列表，搜索后替换为结果
- 归档页时间线：垂直连线 + 圆点 + 卡片，hover 右移动效
- 响应式：≤768px 卡片改为上下堆叠

### v3 — 2026-03-24 个人中心 + 轮播图 + 主页三栏布局

#### 新增功能
- **个人中心（`Profile.vue`）**：顶部渐变头像卡片 + 基本信息展示 + 编辑资料表单 + 修改密码弹窗
  - 支持修改昵称、邮箱、手机号、头像
  - 修改密码需验证旧密码，新密码6-20位，二次确认
  - 修改后同步更新 Pinia store 和 localStorage（顶栏即时刷新）
- **首页轮播图**：el-carousel 展示置顶热点新闻，4秒自动切换，封面背景图 + 渐变遮罩 + 标题摘要
- **用户下拉菜单**：新增「个人中心」入口

#### 主页布局改版（v2 → v3）
| | v2 | v3 |
|---|---|---|
| 布局 | 双栏（中+右） | 三栏（左分类 / 中新闻 / 右热门+公告） |
| 热点展示 | 3格卡片 | 轮播图（el-carousel） |
| 分类筛选 | pill按钮横排 | 左侧栏高亮列表 |
| 新闻卡片 | 双列网格 | 单列横向卡片（左图右文） |
| 右侧栏 | 统计+分类+TOP5 | 热门TOP8 + 公告 |
| 响应式 | ≤1024单列 | ≤1200隐藏左栏，≤768单列 |

#### 后端新增
- `UpdateProfileDTO.java`：修改个人信息请求体
- `ChangePasswordDTO.java`：修改密码请求体（含 @Valid 校验）
- `PUT /api/auth/profile`：修改个人信息接口
- `PUT /api/auth/password`：修改密码接口
- `UserService.updateProfile()` / `UserServiceImpl.updateProfile()`：更新用户信息
- `UserService.changePassword(ChangePasswordDTO)`：密码修改（BCrypt验证旧密码）

#### 前端新增/修改
- **新增** `Profile.vue`：个人中心页面
- `Home.vue`：全面重写，新增轮播图 + 三栏布局
- `Layout.vue`：下拉菜单增加个人中心入口
- `router/index.js`：新增 `/profile` 路由（requiresAuth）
- `stores/user.js`：新增 `updateUserInfo()` 方法
- `api/auth.js`：新增 `updateProfile`、`changePassword` API

### v2 — 2026-03-24 前端美化 + 数据库种子数据

#### 前端美化（6 个文件重写）
- **App.vue**：全局样式重置、自定义滚动条、统一主色调 (#4f6ef7)
- **Layout.vue**：毛玻璃顶栏 + 品牌Logo区 + 导航高亮 + 用户头像下拉菜单（显示角色标签）+ 丰富 Footer
- **Home.vue**（改动最大）：
  - Hero 统计区（渐变背景 + 新闻数/分类数/用户数）
  - 热点推荐：3 格卡片式置顶新闻
  - 分类筛选：pill 按钮式（带 emoji 图标）
  - 新闻列表：双列卡片网格（封面图 + 标签 + 摘要 + 元信息）
  - 侧边栏：数据统计 / 分类快捷入口 / 热门排行 TOP 5
  - 响应式布局（≤1024px 单列，≤768px 全部堆叠）
- **NewsDetail.vue**：面包屑导航、左右布局（正文 + 侧边信息卡）、头像化评论区、优雅点赞按钮
- **Login.vue**：左右分栏布局（左侧品牌展示 + 渐变背景，右侧表单）
- **Register.vue**：同样左右分栏，青绿渐变 + 注册福利展示

#### 数据库种子数据（seed_data.sql，新文件）
- 15 篇新闻文章（每分类 2-3 篇，含完整 HTML 正文）
- 20 条评论（状态均为"已通过"）
- 4 位测试用户（zhangsan/lisi/wangwu/xiaoming，密码 admin123）

---

### 历史修复

1. ~~BCryptPasswordEncoder 找不到~~ → 添加了 `spring-security-crypto` 依赖
2. ~~application-dev.yml 未提交~~ → 从 .gitignore 中移除并补充提交

## 十一、Git 仓库

- 地址：`https://github.com/142093601/yb`
- 分支：`master`
- 前后端在同一仓库，分 `campus-news-backend/` 和 `campus-news-frontend/` 两个文件夹
