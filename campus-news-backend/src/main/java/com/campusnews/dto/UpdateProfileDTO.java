package com.campusnews.dto;

import lombok.Data;

/**
 * 修改个人信息
 */
@Data
public class UpdateProfileDTO {

    /** 昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;
}
