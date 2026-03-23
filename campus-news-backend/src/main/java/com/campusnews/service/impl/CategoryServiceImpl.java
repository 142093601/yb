package com.campusnews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campusnews.common.PageResult;
import com.campusnews.common.Result;
import com.campusnews.entity.Category;
import com.campusnews.mapper.CategoryMapper;
import com.campusnews.mapper.NewsMapper;
import com.campusnews.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final NewsMapper newsMapper;

    @Override
    public Result<?> listActiveCategories() {
        List<Category> list = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder));
        return Result.success(list);
    }

    @Override
    public Result<?> listAllCategories(Integer page, Integer size) {
        Page<Category> pageParam = new Page<>(page, size);
        Page<Category> result = page(pageParam, new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSortOrder));

        return Result.success(new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getPages()
        ));
    }

    @Override
    public Result<?> addCategory(Category category) {
        long count = count(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, category.getName()));
        if (count > 0) {
            return Result.error("分类名称已存在");
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        save(category);
        return Result.success("添加成功", category);
    }

    @Override
    public Result<?> updateCategory(Category category) {
        if (!updateById(category)) {
            return Result.error("分类不存在");
        }
        return Result.success("更新成功");
    }

    @Override
    public Result<?> deleteCategory(Long id) {
        // 检查分类下是否有新闻
        long newsCount = newsMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.campusnews.entity.News>()
                        .eq("category_id", id));
        if (newsCount > 0) {
            return Result.error("该分类下还有" + newsCount + "条新闻，不能删除");
        }
        removeById(id);
        return Result.success("删除成功");
    }
}
