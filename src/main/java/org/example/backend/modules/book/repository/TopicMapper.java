package org.example.backend.modules.book.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.Topic;
import org.apache.ibatis.annotations.Mapper;

/**
 * 主题Mapper
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
}


