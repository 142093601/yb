package com.campusnews.controller;

import com.campusnews.common.Result;
import com.campusnews.dto.NewsDTO;
import com.campusnews.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台新闻管理接口
 */
@Tag(name = "新闻管理（后台）")
@RestController
@RequestMapping("/api/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;

    @Operation(summary = "分页获取所有新闻")
    @GetMapping("/list")
    public Result<?> listAllNews(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return newsService.listAllNews(keyword, status, page, size);
    }

    @Operation(summary = "保存新闻（新增/编辑）")
    @PostMapping("/save")
    public Result<?> saveNews(@Valid @RequestBody NewsDTO newsDTO, HttpServletRequest request) {
        Long authorId = (Long) request.getAttribute("userId");
        return newsService.saveNews(newsDTO, authorId);
    }

    @Operation(summary = "删除新闻")
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteNews(@PathVariable Long id) {
        return newsService.deleteNews(id);
    }

    @Operation(summary = "发布/下架新闻")
    @PutMapping("/publish/{id}")
    public Result<?> togglePublish(@PathVariable Long id, @RequestParam Integer status) {
        return newsService.togglePublishStatus(id, status);
    }

    @Operation(summary = "置顶/取消置顶")
    @PutMapping("/top/{id}")
    public Result<?> toggleTop(@PathVariable Long id) {
        return newsService.toggleTop(id);
    }
}
