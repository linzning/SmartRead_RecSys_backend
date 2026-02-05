package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 图书主题关联表
 */
@Data
@TableName("book_topics")
public class BookTopic {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 主题名称（如：人工智能、Python、文学等）
     */
    private String topicName;
}

