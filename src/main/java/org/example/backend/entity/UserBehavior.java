package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户行为日志实体
 */
@Data
@TableName("user_behaviors")
public class UserBehavior {
    
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
     * 行为类型：VIEW-浏览, FAVORITE-收藏, RATING-评分, BORROW-借阅
     */
    private String behaviorType;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 行为数据（JSON格式）
     */
    private String behaviorData;
    
    /**
     * 行为时间
     */
    private LocalDateTime createTime;
}



