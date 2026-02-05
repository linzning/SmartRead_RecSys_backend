package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图书冷启动配置实体
 */
@Data
@TableName("book_cold_start")
public class BookColdStart {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookId;

    /**
     * 初始曝光量（运营目标）
     */
    private Integer initialExposure;

    /**
     * 手动权重扶持（0-1）
     */
    private Double manualWeight;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer isEnabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}



