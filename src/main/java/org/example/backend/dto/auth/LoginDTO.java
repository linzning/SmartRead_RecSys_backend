package org.example.backend.dto.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录DTO
 */
@Data
public class LoginDTO {
    
    @NotBlank(message = "用户名或邮箱不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}

