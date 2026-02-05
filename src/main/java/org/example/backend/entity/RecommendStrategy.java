package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐策略配置实体
 */
@Data
@TableName("recommend_strategy")
public class RecommendStrategy {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 策略键
     */
    private String strategyKey;
    
    /**
     * 策略值（JSON格式）
     */
    private String strategyValue;
    
    /**
     * 策略描述
     */
    private String description;
    
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

