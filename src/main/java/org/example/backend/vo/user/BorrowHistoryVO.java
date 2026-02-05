package org.example.backend.vo.user;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 借阅历史VO
 */
@Data
public class BorrowHistoryVO {
    
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCoverUrl;
    private LocalDateTime applyTime;
    private LocalDateTime borrowTime;
    private LocalDateTime expectedReturnTime;
    private LocalDateTime returnApplyTime;
    private LocalDateTime returnTime;
    private Integer status;
    private Integer auditStatus;
    private Integer overdueDays;
}


