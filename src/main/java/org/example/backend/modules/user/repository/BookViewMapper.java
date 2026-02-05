package org.example.backend.modules.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.backend.entity.BookView;

/**
 * 图书浏览记录Mapper
 */
@Mapper
public interface BookViewMapper extends BaseMapper<BookView> {
}

