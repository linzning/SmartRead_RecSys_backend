package org.example.backend.vo.recommend;

import lombok.Data;

/**
 * 推荐图书VO（包含推荐理由）
 */
@Data
public class RecommendBookVO {
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 书名
     */
    private String title;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 封面URL
     */
    private String coverUrl;
    
    /**
     * 平均评分
     */
    private Double avgRating;
    
    /**
     * 推荐理由
     */
    private String reason;
}

