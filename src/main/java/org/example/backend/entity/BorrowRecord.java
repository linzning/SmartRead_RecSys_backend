package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 借阅记录实体
 */
@Data
@TableName("borrow_records")
public class BorrowRecord {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 申请时间
     */
    private LocalDateTime applyTime;
    
    /**
     * 借阅时间
     */
    private LocalDateTime borrowTime;
    
    /**
     * 预计归还时间
     */
    private LocalDateTime expectedReturnTime;

    /**
     * 还书申请时间（用户发起归还申请，等待管理员确认）
     */
    private LocalDateTime returnApplyTime;
    
    /**
     * 归还时间
     */
    private LocalDateTime returnTime;
    
    /**
     * 逾期天数
     */
    private Integer overdueDays;
    
    /**
     * 状态：0-已归还，1-借阅中
     */
    private Integer status;
    
    /**
     * 审核状态：0-待审核，1-已通过，2-已拒绝
     */
    private Integer auditStatus;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 审核管理员ID
     */
    private Long auditAdminId;
}

