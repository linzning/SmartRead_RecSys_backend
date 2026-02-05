package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图书浏览记录实体
 */
@Data
@TableName("book_views")
public class BookView {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID（未登录用户为NULL）
     */
    private Long userId;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 浏览时间
     */
    private LocalDateTime viewTime;
    
    /**
     * 浏览时长（秒）
     */
    private Integer duration;
}

