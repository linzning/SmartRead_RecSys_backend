package org.example.backend.modules.admin.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户行为日志Mapper
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
}



