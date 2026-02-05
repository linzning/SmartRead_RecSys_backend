package org.example.backend.vo.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.List;

/**
 * 图书详情VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BookDetailVO extends BookVO {
    
    /**
     * 是否已收藏
     */
    private Boolean isFavorited;
    
    /**
     * 用户评分（如果已评分）
     */
    private Integer userRating;
}

