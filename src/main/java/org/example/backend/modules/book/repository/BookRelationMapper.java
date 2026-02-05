package org.example.backend.modules.book.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.backend.entity.BookRelation;

/**
 * 图书关联关系Mapper
 */
@Mapper
public interface BookRelationMapper extends BaseMapper<BookRelation> {
}

