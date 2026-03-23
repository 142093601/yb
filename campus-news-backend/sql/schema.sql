-- ============================
-- 校园新闻系统 - 数据库建表脚本
-- ============================

CREATE DATABASE IF NOT EXISTS campus_news DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_news;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN/EDITOR/USER',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删, 1-已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 默认管理员账号（密码: admin123）
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `status`)
VALUES ('admin', '$2a$10$mIQOXNlG91Oi7hg.fax7e.Hw5Onqr4ytk36K.ae8XvTBjDVl3/.3q', '系统管理员', 'ADMIN', 1);

-- ----------------------------
-- 新闻分类表
-- ----------------------------
CREATE TABLE `news_category` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        VARCHAR(50) NOT NULL COMMENT '分类名称',
    `sort_order`  INT         NOT NULL DEFAULT 0 COMMENT '排序号',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻分类表';

-- 初始化分类
INSERT INTO `news_category` (`name`, `sort_order`) VALUES
('校园动态', 1),
('通知公告', 2),
('学术科研', 3),
('文体活动', 4),
('就业实习', 5);

-- ----------------------------
-- 新闻文章表
-- ----------------------------
CREATE TABLE `news_article` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `title`        VARCHAR(200)  NOT NULL COMMENT '标题',
    `summary`      VARCHAR(500)  DEFAULT NULL COMMENT '摘要',
    `cover_image`  VARCHAR(255)  DEFAULT NULL COMMENT '封面图片URL',
    `content`      LONGTEXT      NOT NULL COMMENT '正文内容（富文本）',
    `category_id`  BIGINT        NOT NULL COMMENT '分类ID',
    `author_id`    BIGINT        NOT NULL COMMENT '作者ID',
    `author_name`  VARCHAR(50)   DEFAULT NULL COMMENT '作者名称',
    `view_count`   INT           NOT NULL DEFAULT 0 COMMENT '浏览量',
    `like_count`   INT           NOT NULL DEFAULT 0 COMMENT '点赞数',
    `status`       TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0-草稿, 1-已发布, 2-已下架',
    `is_top`       TINYINT       NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否, 1-是',
    `publish_time` DATETIME      DEFAULT NULL COMMENT '发布时间',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`   TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`),
    FULLTEXT KEY `ft_title` (`title`) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻文章表';

-- ----------------------------
-- 评论表
-- ----------------------------
CREATE TABLE `news_comment` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `news_id`        BIGINT        NOT NULL COMMENT '新闻ID',
    `user_id`        BIGINT        NOT NULL COMMENT '用户ID',
    `nickname`       VARCHAR(50)   DEFAULT NULL COMMENT '用户昵称',
    `avatar`         VARCHAR(255)  DEFAULT NULL COMMENT '用户头像',
    `content`        VARCHAR(1000) NOT NULL COMMENT '评论内容',
    `parent_id`      BIGINT        DEFAULT NULL COMMENT '父评论ID',
    `reply_nickname` VARCHAR(50)   DEFAULT NULL COMMENT '回复给谁',
    `status`         TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0-待审核, 1-已通过, 2-已拒绝',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_news_id` (`news_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
