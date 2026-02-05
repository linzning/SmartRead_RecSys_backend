package org.example.backend.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户行为日志VO
 */
@Data
public class UserBehaviorVO {
    
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String behaviorType; // VIEW-浏览, FAVORITE-收藏, RATING-评分, BORROW-借阅
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private String bookCoverUrl;
    private String behaviorData; // JSON格式
    private LocalDateTime createTime;
}



