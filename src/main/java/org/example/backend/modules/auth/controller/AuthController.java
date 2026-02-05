package org.example.backend.modules.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.auth.LoginDTO;
import org.example.backend.dto.auth.RegisterDTO;
import org.example.backend.modules.auth.service.AuthService;
import org.example.backend.vo.auth.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户注册、登录接口")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return ApiResponse.<Void>success("注册成功", null);
    }
    
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return ApiResponse.success("登录成功", loginVO);
    }
}

