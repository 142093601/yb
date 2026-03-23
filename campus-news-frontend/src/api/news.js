import request from '../utils/request'

// 获取已发布新闻列表
export const getNewsList = (params) => request.get('/news/list', { params })
// 获取新闻详情
export const getNewsDetail = (id) => request.get(`/news/detail/${id}`)
// 获取置顶新闻
export const getTopNews = () => request.get('/news/top')
// 点赞
export const likeNews = (id) => request.post(`/news/like/${id}`)
