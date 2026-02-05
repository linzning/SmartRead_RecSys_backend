package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户偏好实体
 */
@Data
@TableName("user_preference")
public class UserPreference {
    
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
     * 偏好类型（topic, author等）
     */
    private String preferenceType;
    
    /**
     * 偏好值（如：人工智能、Python等）
     */
    private String preferenceValue;
    
    /**
     * 权重（0-1）
     */
    private Double weight;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

