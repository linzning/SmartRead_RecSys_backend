package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐曝光记录
 */
@Data
@TableName("recommend_exposure")
public class RecommendExposure {
    
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
     * 推荐类型（home_recommend, similar_book, user_also_read等）
     */
    private String recommendType;
    
    /**
     * 推荐位置（home, detail等）
     */
    private String position;
    
    /**
     * 曝光时间
     */
    private LocalDateTime exposureTime;
}

