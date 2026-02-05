package org.example.backend.modules.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.backend.entity.BooklistBook;

/**
 * 书单图书关联Mapper
 */
@Mapper
public interface BooklistBookMapper extends BaseMapper<BooklistBook> {
}

