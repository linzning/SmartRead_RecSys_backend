package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 主题实体
 */
@Data
@TableName("topics")
public class Topic {
    
    /**
     * 主题ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 主题名称
     */
    private String name;
    
    /**
     * 主题描述
     */
    private String description;
    
    /**
     * 图标类名（Font Awesome）
     */
    private String icon;
    
    /**
     * 渐变色类名（Tailwind CSS）
     */
    private String gradient;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}


