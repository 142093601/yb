package com.campusnews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campusnews.common.JwtUtil;
import com.campusnews.common.PageResult;
import com.campusnews.common.Result;
import com.campusnews.dto.LoginDTO;
import com.campusnews.dto.UserDTO;
import com.campusnews.entity.User;
import com.campusnews.mapper.UserMapper;
import com.campusnews.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Result<?> login(LoginDTO loginDTO) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername()));

        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("role", user.getRole());
        data.put("avatar", user.getAvatar());

        return Result.success("登录成功", data);
    }

    @Override
    public Result<?> register(UserDTO userDTO) {
        // 检查用户名是否已存在
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userDTO.getUsername()));
        if (count > 0) {
            return Result.error("用户名已存在");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setNickname(StringUtils.hasText(userDTO.getNickname()) ? userDTO.getNickname() : userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setRole("USER");
        user.setStatus(1);

        save(user);
        return Result.success("注册成功");
    }

    @Override
    public Result<?> getCurrentUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }

    @Override
    public Result<?> updateUser(UserDTO userDTO) {
        User user = getById(userDTO.getId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (StringUtils.hasText(userDTO.getNickname())) {
            user.setNickname(userDTO.getNickname());
        }
        if (StringUtils.hasText(userDTO.getEmail())) {
            user.setEmail(userDTO.getEmail());
        }
        if (StringUtils.hasText(userDTO.getPhone())) {
            user.setPhone(userDTO.getPhone());
        }
        if (StringUtils.hasText(userDTO.getAvatar())) {
            user.setAvatar(userDTO.getAvatar());
        }
        // 只有管理员能改角色
        if (StringUtils.hasText(userDTO.getRole())) {
            user.setRole(userDTO.getRole());
        }

        updateById(user);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        return Result.success("密码修改成功");
    }

    @Override
    public Result<?> listUsers(String keyword, Integer page, Integer size) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> result = page(pageParam, wrapper);
        // 清除密码
        result.getRecords().forEach(u -> u.setPassword(null));

        return Result.success(new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getPages()
        ));
    }

    @Override
    public Result<?> toggleUserStatus(Long userId) {
        User user = getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        updateById(user);
        return Result.success(user.getStatus() == 1 ? "已启用" : "已禁用");
    }
}
