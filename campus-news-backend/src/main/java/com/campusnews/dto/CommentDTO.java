package com.campusnews.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论请求DTO
 */
@Data
public class CommentDTO {

    @NotNull(message = "新闻ID不能为空")
    private Long newsId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    /** 父评论ID，回复时传入 */
    private Long parentId;
}
