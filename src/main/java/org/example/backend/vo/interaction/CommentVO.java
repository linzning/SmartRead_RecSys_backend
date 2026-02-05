package org.example.backend.vo.interaction;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论VO
 */
@Data
public class CommentVO {
    
    private Long id;
    private Long userId;
    private Long bookId;
    private String username;
    private String avatar;
    private String bookTitle;
    private Integer rating;
    private String content;
    private Integer status; // 0-待审核，1-已通过，2-已拒绝
    private LocalDateTime createTime;
}

