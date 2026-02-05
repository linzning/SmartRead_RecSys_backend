package org.example.backend.modules.recommend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.backend.entity.RecommendStrategy;

/**
 * 推荐策略配置Mapper
 */
@Mapper
public interface RecommendStrategyMapper extends BaseMapper<RecommendStrategy> {
}

