package org.example.backend.modules.recommend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 反馈Mapper
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
}

