package org.example.backend.vo.book;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 图书VO
 */
@Data
public class BookVO {
    
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String publishDate;
    private String coverUrl;
    private String summary;
    private BigDecimal avgRating;
    private Integer ratingCount;
    private Integer borrowCount;
    private Integer favoriteCount;
    private Integer status; // 状态：0-下架，1-上架
    private List<String> topics; // 主题列表
}

