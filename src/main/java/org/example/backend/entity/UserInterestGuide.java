package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户兴趣引导记录实体
 */
@Data
@TableName("user_interest_guide")
public class UserInterestGuide {
    
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
     * 选择的兴趣主题（JSON数组）
     */
    private String selectedTopics;
    
    /**
     * 选择的作者（JSON数组）
     */
    private String selectedAuthors;
    
    /**
     * 是否完成引导：0-未完成，1-已完成
     */
    private Integer isCompleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

