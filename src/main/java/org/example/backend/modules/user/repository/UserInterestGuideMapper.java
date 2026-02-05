package org.example.backend.modules.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.backend.entity.UserInterestGuide;

/**
 * 用户兴趣引导Mapper
 */
@Mapper
public interface UserInterestGuideMapper extends BaseMapper<UserInterestGuide> {
}

