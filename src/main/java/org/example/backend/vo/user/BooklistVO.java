package org.example.backend.vo.user;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 书单VO
 */
@Data
public class BooklistVO {
    
    /**
     * 书单ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 书单名称
     */
    private String name;
    
    /**
     * 书单描述
     */
    private String description;
    
    /**
     * 是否公开
     */
    private Integer isPublic;
    
    /**
     * 图书数量
     */
    private Integer bookCount;
    
    /**
     * 图书列表
     */
    private List<BookSimpleVO> books;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 图书简单信息VO
     */
    @Data
    public static class BookSimpleVO {
        private Long id;
        private String title;
        private String author;
        private String coverUrl;
        private Double avgRating;
    }
}

