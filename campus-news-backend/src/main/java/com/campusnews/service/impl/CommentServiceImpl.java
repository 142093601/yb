package com.campusnews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campusnews.common.PageResult;
import com.campusnews.common.Result;
import com.campusnews.dto.CommentDTO;
import com.campusnews.entity.Comment;
import com.campusnews.entity.User;
import com.campusnews.mapper.CommentMapper;
import com.campusnews.mapper.UserMapper;
import com.campusnews.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final UserMapper userMapper;

    @Override
    public Result<?> listComments(Long newsId, Integer page, Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);
        Page<Comment> result = page(pageParam, new LambdaQueryWrapper<Comment>()
                .eq(Comment::getNewsId, newsId)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime));

        return Result.success(new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getPages()
        ));
    }

    @Override
    public Result<?> addComment(CommentDTO commentDTO, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Comment comment = new Comment();
        comment.setNewsId(commentDTO.getNewsId());
        comment.setUserId(userId);
        comment.setNickname(user.getNickname());
        comment.setAvatar(user.getAvatar());
        comment.setContent(commentDTO.getContent());
        comment.setParentId(commentDTO.getParentId());
        comment.setStatus(0); // 待审核

        // 如果是回复，获取被回复人的昵称
        if (commentDTO.getParentId() != null) {
            Comment parentComment = getById(commentDTO.getParentId());
            if (parentComment != null) {
                comment.setReplyNickname(parentComment.getNickname());
            }
        }

        save(comment);
        return Result.success("评论成功，等待审核");
    }

    @Override
    public Result<?> listAllComments(Integer status, Integer page, Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        wrapper.orderByDesc(Comment::getCreateTime);

        Page<Comment> result = page(pageParam, wrapper);
        return Result.success(new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getPages()
        ));
    }

    @Override
    public Result<?> auditComment(Long id, Integer status) {
        Comment comment = getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        comment.setStatus(status);
        updateById(comment);
        return Result.success("审核完成");
    }

    @Override
    public Result<?> deleteComment(Long id) {
        removeById(id);
        return Result.success("删除成功");
    }
}
