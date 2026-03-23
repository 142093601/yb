package com.campusnews.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新闻/文章
 */
@Data
@TableName("news_article")
public class News {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 摘要 */
    private String summary;

    /** 封面图片URL */
    private String coverImage;

    /** 正文内容（富文本） */
    private String content;

    /** 分类ID */
    private Long categoryId;

    /** 作者ID */
    private Long authorId;

    /** 作者名称（冗余） */
    private String authorName;

    /** 浏览量 */
    private Integer viewCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 状态：0-草稿, 1-已发布, 2-已下架 */
    private Integer status;

    /** 是否置顶：0-否, 1-是 */
    private Integer isTop;

    /** 发布时间 */
    private LocalDateTime publishTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;

    /** 非数据库字段：分类名称 */
    @TableField(exist = false)
    private String categoryName;
}
