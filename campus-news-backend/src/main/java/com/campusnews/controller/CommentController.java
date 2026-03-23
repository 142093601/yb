package com.campusnews.controller;

import com.campusnews.common.Result;
import com.campusnews.dto.CommentDTO;
import com.campusnews.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评论接口（前台+后台）
 */
@Tag(name = "评论管理")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "获取新闻的评论列表（前台）")
    @GetMapping("/comments/list/{newsId}")
    public Result<?> listComments(
            @PathVariable Long newsId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return commentService.listComments(newsId, page, size);
    }

    @Operation(summary = "添加评论（需登录）")
    @PostMapping("/comments/add")
    public Result<?> addComment(@Valid @RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return commentService.addComment(commentDTO, userId);
    }

    @Operation(summary = "获取所有评论（后台）")
    @GetMapping("/admin/comments/list")
    public Result<?> listAllComments(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return commentService.listAllComments(status, page, size);
    }

    @Operation(summary = "审核评论（后台）")
    @PutMapping("/admin/comments/audit/{id}")
    public Result<?> auditComment(@PathVariable Long id, @RequestParam Integer status) {
        return commentService.auditComment(id, status);
    }

    @Operation(summary = "删除评论（后台）")
    @DeleteMapping("/admin/comments/delete/{id}")
    public Result<?> deleteComment(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }
}
