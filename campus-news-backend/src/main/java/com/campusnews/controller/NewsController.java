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
 * 前台新闻接口
 */
@Tag(name = "新闻（前台）")
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "获取已发布新闻列表")
    @GetMapping("/list")
    public Result<?> listNews(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return newsService.listPublishedNews(categoryId, keyword, page, size);
    }

    @Operation(summary = "获取新闻详情")
    @GetMapping("/detail/{id}")
    public Result<?> getNewsDetail(@PathVariable Long id) {
        return newsService.getNewsDetail(id);
    }

    @Operation(summary = "获取置顶新闻")
    @GetMapping("/top")
    public Result<?> getTopNews() {
        return newsService.getTopNews();
    }

    @Operation(summary = "点赞新闻")
    @PostMapping("/like/{id}")
    public Result<?> likeNews(@PathVariable Long id) {
        return newsService.likeNews(id);
    }
}
