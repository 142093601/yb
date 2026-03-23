package com.campusnews.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campusnews.common.Result;
import com.campusnews.dto.NewsDTO;
import com.campusnews.entity.News;

public interface NewsService extends IService<News> {

    /** 前台：分页获取已发布新闻列表 */
    Result<?> listPublishedNews(Long categoryId, String keyword, Integer page, Integer size);

    /** 前台：获取新闻详情 */
    Result<?> getNewsDetail(Long id);

    /** 前台：获取置顶新闻 */
    Result<?> getTopNews();

    /** 后台：分页获取所有新闻 */
    Result<?> listAllNews(String keyword, Integer status, Integer page, Integer size);

    /** 添加/编辑新闻 */
    Result<?> saveNews(NewsDTO newsDTO, Long authorId);

    /** 删除新闻 */
    Result<?> deleteNews(Long id);

    /** 发布/下架新闻 */
    Result<?> togglePublishStatus(Long id, Integer status);

    /** 置顶/取消置顶 */
    Result<?> toggleTop(Long id);

    /** 点赞 */
    Result<?> likeNews(Long id);
}
