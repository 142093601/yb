import request from '../utils/request'

// 获取启用的分类列表
export const getCategories = () => request.get('/category/list')

// --- 后台 ---
// 获取所有分类
export const getAllCategories = (params) => request.get('/admin/category/list', { params })
// 添加分类
export const addCategory = (data) => request.post('/admin/category/add', data)
// 更新分类
export const updateCategory = (data) => request.put('/admin/category/update', data)
// 删除分类
export const deleteCategory = (id) => request.delete(`/admin/category/delete/${id}`)
