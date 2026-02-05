package org.example.backend.modules.recommend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.RecommendClick;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推荐点击Mapper
 */
@Mapper
public interface RecommendClickMapper extends BaseMapper<RecommendClick> {
}

