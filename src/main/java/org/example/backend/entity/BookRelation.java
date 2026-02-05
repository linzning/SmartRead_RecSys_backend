package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图书关联关系实体（知识图谱）
 */
@Data
@TableName("book_relations")
public class BookRelation {
    
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
     * 关联图书ID
     */
    private Long relatedBookId;
    
    /**
     * 关联类型（similar, sequel, prequel, series等）
     */
    private String relationType;
    
    /**
     * 关联权重（0-1）
     */
    private Double weight;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

