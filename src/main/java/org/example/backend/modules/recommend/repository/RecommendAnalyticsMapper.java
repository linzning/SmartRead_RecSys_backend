package org.example.backend.modules.recommend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.backend.entity.RecommendAnalytics;

/**
 * 推荐效果分析Mapper
 */
@Mapper
public interface RecommendAnalyticsMapper extends BaseMapper<RecommendAnalytics> {
}

