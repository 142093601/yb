package com.campusnews.controller;

import com.campusnews.common.Result;
import com.campusnews.dto.UserDTO;
import com.campusnews.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台用户管理接口
 */
@Tag(name = "用户管理（后台）")
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户")
    @GetMapping("/list")
    public Result<?> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return userService.listUsers(keyword, page, size);
    }

    @Operation(summary = "编辑用户")
    @PutMapping("/update")
    public Result<?> updateUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/toggle/{id}")
    public Result<?> toggleUserStatus(@PathVariable Long id) {
        return userService.toggleUserStatus(id);
    }
}
