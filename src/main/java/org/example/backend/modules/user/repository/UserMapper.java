package org.example.backend.modules.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(String username);
    
    /**
     * 根据邮箱查询
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User selectByEmail(String email);
}

