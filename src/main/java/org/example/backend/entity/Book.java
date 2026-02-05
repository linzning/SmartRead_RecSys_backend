package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 图书实体
 */
@Data
@TableName("books")
public class Book {
    
    /**
     * 图书ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * ISBN
     */
    private String isbn;
    
    /**
     * 书名
     */
    private String title;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 出版社
     */
    private String publisher;
    
    /**
     * 出版日期
     */
    private String publishDate;
    
    /**
     * 封面URL
     */
    private String coverUrl;
    
    /**
     * 摘要
     */
    private String summary;
    
    /**
     * 平均评分
     */
    private BigDecimal avgRating;
    
    /**
     * 评分人数
     */
    private Integer ratingCount;
    
    /**
     * 借阅次数
     */
    private Integer borrowCount;
    
    /**
     * 收藏次数
     */
    private Integer favoriteCount;
    
    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

