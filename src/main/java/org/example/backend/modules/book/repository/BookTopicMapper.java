package org.example.backend.modules.book.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.BookTopic;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书主题Mapper
 */
@Mapper
public interface BookTopicMapper extends BaseMapper<BookTopic> {
}

