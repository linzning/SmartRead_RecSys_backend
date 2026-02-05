package org.example.backend.vo.admin;

import lombok.Data;

/**
 * 推荐策略配置VO
 */
@Data
public class RecommendStrategyVO {
    
    /**
     * 热门推荐比例（0-1）
     */
    private Double hotRecommendRatio;
    
    /**
     * 全局多样性权重（0-1）
     */
    private Double globalDiversityWeight;
    
    /**
     * 冷启动策略：新用户推荐热门图书的比例（0-1）
     */
    private Double coldStartHotRatio;
    
    /**
     * 长尾图书扶持比例（0-1）
     */
    private Double longTailRatio;
    
    /**
     * 长尾图书定义：借阅次数小于此值的图书
     */
    private Integer longTailThreshold;
    
    /**
     * 是否启用长尾推荐
     */
    private Boolean enableLongTail;
}

