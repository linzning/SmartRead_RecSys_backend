package org.example.backend.vo.auth;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginVO {
    
    /**
     * Token
     */
    private String token;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 角色
     */
    private String role;
}

