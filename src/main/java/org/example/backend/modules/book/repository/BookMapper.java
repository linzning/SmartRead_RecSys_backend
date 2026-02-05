package org.example.backend.modules.book.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.Book;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书Mapper
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
}

