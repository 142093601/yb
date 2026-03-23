package com.campusnews.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campusnews.common.Result;
import com.campusnews.dto.CommentDTO;
import com.campusnews.entity.Comment;

public interface CommentService extends IService<Comment> {

    /** 获取新闻的评论列表（已通过的） */
    Result<?> listComments(Long newsId, Integer page, Integer size);

    /** 添加评论 */
    Result<?> addComment(CommentDTO commentDTO, Long userId);

    /** 后台：获取所有评论（含待审核） */
    Result<?> listAllComments(Integer status, Integer page, Integer size);

    /** 审核评论 */
    Result<?> auditComment(Long id, Integer status);

    /** 删除评论 */
    Result<?> deleteComment(Long id);
}
