import request from '../utils/request'

// --- 新闻管理 ---
export const getAdminNewsList = (params) => request.get('/admin/news/list', { params })
export const saveNews = (data) => request.post('/admin/news/save', data)
export const deleteNews = (id) => request.delete(`/admin/news/delete/${id}`)
export const publishNews = (id, status) => request.put(`/admin/news/publish/${id}`, null, { params: { status } })
export const topNews = (id) => request.put(`/admin/news/top/${id}`)

// --- 用户管理 ---
export const getUserList = (params) => request.get('/admin/user/list', { params })
export const updateUser = (data) => request.put('/admin/user/update', data)
export const toggleUserStatus = (id) => request.put(`/admin/user/toggle/${id}`)

// --- 文件上传 ---
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
