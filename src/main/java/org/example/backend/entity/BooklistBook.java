package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书单图书关联实体
 */
@Data
@TableName("booklist_books")
public class BooklistBook {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 书单ID
     */
    private Long booklistId;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 添加时间
     */
    private LocalDateTime addTime;
}

