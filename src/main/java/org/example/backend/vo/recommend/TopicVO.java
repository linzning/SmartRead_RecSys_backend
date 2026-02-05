package org.example.backend.vo.recommend;

import lombok.Data;

/**
 * 主题VO
 */
@Data
public class TopicVO {
    
    /**
     * 主题名称
     */
    private String name;
    
    /**
     * 主题描述
     */
    private String description;
    
    /**
     * 该主题下的图书数量
     */
    private Long bookCount;
    
    /**
     * 图标（前端使用）
     */
    private String icon;
    
    /**
     * 渐变色（前端使用）
     */
    private String gradient;
}


