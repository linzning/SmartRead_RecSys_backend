package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈实体（不感兴趣、反馈等）
 */
@Data
@TableName("feedback")
public class Feedback {
    
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
     * 反馈类型（not_interested, negative_feedback等）
     */
    private String feedbackType;
    
    /**
     * 反馈原因
     */
    private String reason;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

