package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐解释详情实体
 */
@Data
@TableName("recommend_reasons")
public class RecommendReason {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 推荐类型
     */
    private String recommendType;
    
    /**
     * 理由类型（borrow_history, topic_match, rating_rank等）
     */
    private String reasonType;
    
    /**
     * 理由内容（JSON格式）
     */
    private String reasonContent;
    
    /**
     * 理由权重
     */
    private Double weight;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

