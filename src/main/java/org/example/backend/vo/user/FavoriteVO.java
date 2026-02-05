package org.example.backend.vo.user;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏VO
 */
@Data
public class FavoriteVO {
    
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCoverUrl;
    private BigDecimal bookAvgRating;
    private LocalDateTime createTime;
}


