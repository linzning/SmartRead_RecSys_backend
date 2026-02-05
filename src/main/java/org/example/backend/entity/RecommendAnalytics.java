package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 推荐效果分析实体
 */
@Data
@TableName("recommend_analytics")
public class RecommendAnalytics {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 推荐类型（home, similar, user_also_read等）
     */
    private String recommendType;
    
    /**
     * 统计日期
     */
    private LocalDate date;
    
    /**
     * 曝光次数
     */
    private Long exposureCount;
    
    /**
     * 点击次数
     */
    private Long clickCount;
    
    /**
     * 点击率
     */
    private Double ctr;
    
    /**
     * 平均评分
     */
    private Double avgRating;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

