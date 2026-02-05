package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐点击记录
 */
@Data
@TableName("recommend_click")
public class RecommendClick {
    
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
     * 推荐类型
     */
    private String recommendType;
    
    /**
     * 推荐位置
     */
    private String position;
    
    /**
     * 点击时间
     */
    private LocalDateTime clickTime;
}

