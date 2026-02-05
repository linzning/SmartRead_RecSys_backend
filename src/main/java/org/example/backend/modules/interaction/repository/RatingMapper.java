package org.example.backend.modules.interaction.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.Rating;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评分Mapper
 */
@Mapper
public interface RatingMapper extends BaseMapper<Rating> {
}

