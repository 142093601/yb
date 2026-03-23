package com.campusnews.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论
 */
@Data
@TableName("news_comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 新闻ID */
    private Long newsId;

    /** 用户ID */
    private Long userId;

    /** 用户昵称（冗余） */
    private String nickname;

    /** 用户头像（冗余） */
    private String avatar;

    /** 评论内容 */
    private String content;

    /** 父评论ID（回复某条评论时使用） */
    private Long parentId;

    /** 回复给谁的昵称 */
    private String replyNickname;

    /** 状态：0-待审核, 1-已通过, 2-已拒绝 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer isDeleted;
}
