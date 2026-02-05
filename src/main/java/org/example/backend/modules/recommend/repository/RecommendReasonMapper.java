package org.example.backend.modules.recommend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.backend.entity.RecommendReason;

/**
 * 推荐解释详情Mapper
 */
@Mapper
public interface RecommendReasonMapper extends BaseMapper<RecommendReason> {
}

