package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐位配置实体
 */
@Data
@TableName("recommend_positions")
public class RecommendPosition {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 推荐位标识（home_recommend, new_book等）
     */
    private String positionKey;
    
    /**
     * 推荐位名称
     */
    private String positionName;
    
    /**
     * 推荐图书ID（手动配置）
     */
    private Long bookId;
    
    /**
     * 优先级（数字越大优先级越高）
     */
    private Integer priority;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer isEnabled;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

