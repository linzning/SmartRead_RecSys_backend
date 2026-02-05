package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐黑名单实体
 */
@Data
@TableName("recommend_blacklist")
public class RecommendBlacklist {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookId;

    /**
     * 推荐类型（为空表示全局）
     */
    private String recommendType;

    /**
     * 推荐位（为空表示全位置）
     */
    private String position;

    private String reason;

    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer isEnabled;

    private LocalDateTime createTime;
}



