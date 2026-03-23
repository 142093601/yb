package com.campusnews.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新闻请求DTO
 */
@Data
public class NewsDTO {

    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String summary;

    private String coverImage;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /** 状态：0-草稿, 1-发布 */
    private Integer status;

    /** 是否置顶 */
    private Integer isTop;
}
