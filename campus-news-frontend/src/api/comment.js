import request from '../utils/request'

// 获取新闻评论
export const getComments = (newsId, params) => request.get(`/comments/list/${newsId}`, { params })
// 添加评论
export const addComment = (data) => request.post('/comments/add', data)

// --- 后台 ---
// 获取所有评论
export const getAllComments = (params) => request.get('/admin/comments/list', { params })
// 审核评论
export const auditComment = (id, status) => request.put(`/admin/comments/audit/${id}`, null, { params: { status } })
// 删除评论
export const deleteComment = (id) => request.delete(`/admin/comments/delete/${id}`)
