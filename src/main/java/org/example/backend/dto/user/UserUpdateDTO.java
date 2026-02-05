package org.example.backend.dto.user;

import lombok.Data;

/**
 * 用户更新DTO
 */
@Data
public class UserUpdateDTO {
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 新密码
     */
    private String password;
    
    /**
     * 头像URL
     */
    private String avatar;
}


