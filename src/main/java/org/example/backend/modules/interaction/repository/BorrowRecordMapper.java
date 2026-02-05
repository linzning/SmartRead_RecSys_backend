package org.example.backend.modules.interaction.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 借阅记录Mapper
 */
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
}

