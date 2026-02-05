package org.example.backend.modules.auth.service;

import org.example.backend.dto.auth.LoginDTO;
import org.example.backend.dto.auth.RegisterDTO;
import org.example.backend.vo.auth.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);
    
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);
}

