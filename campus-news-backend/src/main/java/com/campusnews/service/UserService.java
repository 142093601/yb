package com.campusnews.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campusnews.common.Result;
import com.campusnews.dto.LoginDTO;
import com.campusnews.dto.UserDTO;
import com.campusnews.entity.User;

public interface UserService extends IService<User> {

    /** 登录 */
    Result<?> login(LoginDTO loginDTO);

    /** 注册 */
    Result<?> register(UserDTO userDTO);

    /** 获取当前登录用户信息 */
    Result<?> getCurrentUser(Long userId);

    /** 更新用户信息 */
    Result<?> updateUser(UserDTO userDTO);

    /** 修改密码 */
    Result<?> changePassword(Long userId, String oldPassword, String newPassword);

    /** 管理员：分页查询用户 */
    Result<?> listUsers(String keyword, Integer page, Integer size);

    /** 管理员：禁用/启用用户 */
    Result<?> toggleUserStatus(Long userId);
}
