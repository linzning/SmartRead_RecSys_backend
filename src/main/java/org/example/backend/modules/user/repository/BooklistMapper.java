package org.example.backend.modules.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.backend.entity.Booklist;

/**
 * 书单Mapper
 */
@Mapper
public interface BooklistMapper extends BaseMapper<Booklist> {
}

