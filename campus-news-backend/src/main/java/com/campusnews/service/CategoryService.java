package com.campusnews.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campusnews.common.Result;
import com.campusnews.entity.Category;

public interface CategoryService extends IService<Category> {

    /** 获取所有启用的分类（前台） */
    Result<?> listActiveCategories();

    /** 获取所有分类（后台） */
    Result<?> listAllCategories(Integer page, Integer size);

    /** 添加分类 */
    Result<?> addCategory(Category category);

    /** 更新分类 */
    Result<?> updateCategory(Category category);

    /** 删除分类 */
    Result<?> deleteCategory(Long id);
}
