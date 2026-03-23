package com.campusnews.controller;

import com.campusnews.common.Result;
import com.campusnews.entity.Category;
import com.campusnews.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 分类接口（前台+后台）
 */
@Tag(name = "分类管理")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取启用的分类列表（前台）")
    @GetMapping("/category/list")
    public Result<?> listActiveCategories() {
        return categoryService.listActiveCategories();
    }

    @Operation(summary = "获取所有分类（后台）")
    @GetMapping("/admin/category/list")
    public Result<?> listAllCategories(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.listAllCategories(page, size);
    }

    @Operation(summary = "添加分类")
    @PostMapping("/admin/category/add")
    public Result<?> addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/admin/category/update")
    public Result<?> updateCategory(@RequestBody Category category) {
        return categoryService.updateCategory(category);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/admin/category/delete/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}
