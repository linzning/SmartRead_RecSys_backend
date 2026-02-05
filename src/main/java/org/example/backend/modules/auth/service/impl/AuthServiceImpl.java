package org.example.backend.modules.auth.service.impl;

import org.example.backend.common.constants.Constants;
import org.example.backend.common.exception.BusinessException;
import org.example.backend.dto.auth.LoginDTO;
import org.example.backend.dto.auth.RegisterDTO;
import org.example.backend.entity.Role;
import org.example.backend.entity.User;
import org.example.backend.entity.UserRole;
import org.example.backend.modules.auth.service.AuthService;
import org.example.backend.modules.user.repository.RoleMapper;
import org.example.backend.modules.user.repository.UserMapper;
import org.example.backend.modules.user.repository.UserRoleMapper;
import org.example.backend.vo.auth.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private org.example.backend.config.JwtUtil jwtUtil;
    
    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        User existEmail = userMapper.selectByEmail(registerDTO.getEmail());
        if (existEmail != null) {
            throw new BusinessException("邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setStatus(Constants.USER_STATUS_ENABLED);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        
        // 分配默认角色USER
        Role userRole = roleMapper.selectByName(Constants.ROLE_USER);
        if (userRole != null) {
            UserRole userRoleRelation = new UserRole();
            userRoleRelation.setUserId(user.getId());
            userRoleRelation.setRoleId(userRole.getId());
            userRoleMapper.insert(userRoleRelation);
        }
    }
    
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查找用户（支持用户名或邮箱登录）
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            user = userMapper.selectByEmail(loginDTO.getUsername());
        }
        
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (!Constants.USER_STATUS_ENABLED.equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 获取用户角色
        String role = userRoleMapper.selectRoleByUserId(user.getId());
        if (role == null) {
            role = Constants.ROLE_USER; // 默认角色
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), role);
        
        // 返回登录信息
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRole(role);
        
        return loginVO;
    }
}

