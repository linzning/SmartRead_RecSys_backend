package org.example.backend.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 借阅记录VO（管理员）
 */
@Data
public class BorrowRecordVO {
    
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private String bookCoverUrl;
    private LocalDateTime applyTime;
    private LocalDateTime borrowTime;
    private LocalDateTime expectedReturnTime;
    private LocalDateTime returnTime;
    private Integer overdueDays;
    private Integer status; // 0-已归还，1-借阅中
    private Integer auditStatus; // 0-待审核，1-已通过，2-已拒绝
    private LocalDateTime auditTime;
    private Long auditAdminId;
    private String auditAdminName;
}



