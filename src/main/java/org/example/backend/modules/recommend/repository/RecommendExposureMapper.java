package org.example.backend.modules.recommend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.RecommendExposure;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推荐曝光Mapper
 */
@Mapper
public interface RecommendExposureMapper extends BaseMapper<RecommendExposure> {
}

