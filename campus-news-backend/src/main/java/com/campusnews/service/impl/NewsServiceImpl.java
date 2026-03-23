package com.campusnews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campusnews.common.PageResult;
import com.campusnews.common.Result;
import com.campusnews.dto.NewsDTO;
import com.campusnews.entity.Category;
import com.campusnews.entity.News;
import com.campusnews.mapper.CategoryMapper;
import com.campusnews.mapper.NewsMapper;
import com.campusnews.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    private final CategoryMapper categoryMapper;

    @Override
    public Result<?> listPublishedNews(Long categoryId, String keyword, Integer page, Integer size) {
        Page<News> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<News>()
                .eq(News::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(News::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(News::getTitle, keyword);
        }
        wrapper.orderByDesc(News::getIsTop)
                .orderByDesc(News::getPublishTime);

        Page<News> result = page(pageParam, wrapper);

        // 填充分类名称
        fillCategoryNames(result.getRecords());

        return Result.success(new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getPages()
        ));
    }

    @Override
    public Result<?> getNewsDetail(Long id) {
        News news = getById(id);
        if (news == null || news.getStatus() != 1) {
            return Result.error("新闻不存在或已下架");
        }

        // 浏览量 +1
        update(new LambdaUpdateWrapper<News>()
                .eq(News::getId, id)
                .setSql("view_count = view_count + 1"));

        news.setViewCount(news.getViewCount() + 1);
        fillCategoryName(news);

        return Result.success(news);
    }

    @Override
    public Result<?> getTopNews() {
        List<News> list = list(new LambdaQueryWrapper<News>()
                .eq(News::getStatus, 1)
                .eq(News::getIsTop, 1)
                .orderByDesc(News::getPublishTime)
                .last("LIMIT 5"));
        fillCategoryNames(list);
        return Result.success(list);
    }

    @Override
    public Result<?> listAllNews(String keyword, Integer status, Integer page, Integer size) {
        Page<News> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(News::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(News::getStatus, status);
        }
        wrapper.orderByDesc(News::getCreateTime);

        Page<News> result = page(pageParam, wrapper);
        fillCategoryNames(result.getRecords());

        return Result.success(new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getPages()
        ));
    }

    @Override
    public Result<?> saveNews(NewsDTO newsDTO, Long authorId) {
        News news = new News();
        news.setId(newsDTO.getId());
        news.setTitle(newsDTO.getTitle());
        news.setSummary(newsDTO.getSummary());
        news.setCoverImage(newsDTO.getCoverImage());
        news.setContent(newsDTO.getContent());
        news.setCategoryId(newsDTO.getCategoryId());
        news.setAuthorId(authorId);
        news.setStatus(newsDTO.getStatus() != null ? newsDTO.getStatus() : 0);
        news.setIsTop(newsDTO.getIsTop() != null ? newsDTO.getIsTop() : 0);

        if (news.getId() == null) {
            // 新增
            news.setViewCount(0);
            news.setLikeCount(0);
            if (news.getStatus() == 1) {
                news.setPublishTime(LocalDateTime.now());
            }
            save(news);
            return Result.success("创建成功", news);
        } else {
            // 编辑
            if (news.getStatus() == 1) {
                news.setPublishTime(LocalDateTime.now());
            }
            updateById(news);
            return Result.success("更新成功", news);
        }
    }

    @Override
    public Result<?> deleteNews(Long id) {
        removeById(id);
        return Result.success("删除成功");
    }

    @Override
    public Result<?> togglePublishStatus(Long id, Integer status) {
        News news = getById(id);
        if (news == null) {
            return Result.error("新闻不存在");
        }
        news.setStatus(status);
        if (status == 1 && news.getPublishTime() == null) {
            news.setPublishTime(LocalDateTime.now());
        }
        updateById(news);
        return Result.success(status == 1 ? "已发布" : "已下架");
    }

    @Override
    public Result<?> toggleTop(Long id) {
        News news = getById(id);
        if (news == null) {
            return Result.error("新闻不存在");
        }
        news.setIsTop(news.getIsTop() == 1 ? 0 : 1);
        updateById(news);
        return Result.success(news.getIsTop() == 1 ? "已置顶" : "已取消置顶");
    }

    @Override
    public Result<?> likeNews(Long id) {
        update(new LambdaUpdateWrapper<News>()
                .eq(News::getId, id)
                .setSql("like_count = like_count + 1"));
        return Result.success("点赞成功");
    }

    /** 批量填充分类名称 */
    private void fillCategoryNames(List<News> newsList) {
        if (newsList.isEmpty()) return;
        List<Long> categoryIds = newsList.stream()
                .map(News::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        newsList.forEach(n -> n.setCategoryName(categoryMap.get(n.getCategoryId())));
    }

    private void fillCategoryName(News news) {
        if (news.getCategoryId() != null) {
            Category category = categoryMapper.selectById(news.getCategoryId());
            if (category != null) {
                news.setCategoryName(category.getName());
            }
        }
    }
}
