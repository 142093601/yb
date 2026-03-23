import request from '../utils/request'

// 登录
export const login = (data) => request.post('/auth/login', data)
// 注册
export const register = (data) => request.post('/auth/register', data)
// 获取当前用户信息
export const getUserInfo = () => request.get('/auth/info')
// 修改个人信息
export const updateProfile = (data) => request.put('/auth/profile', data)
// 修改密码
export const changePassword = (data) => request.put('/auth/password', data)
