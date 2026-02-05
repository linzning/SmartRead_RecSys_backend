package org.example.backend.modules.interaction.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}

