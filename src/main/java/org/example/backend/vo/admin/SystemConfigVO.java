package org.example.backend.vo.admin;

import lombok.Data;

/**
 * 系统配置VO
 */
@Data
public class SystemConfigVO {
    
    /**
     * Redis缓存开关
     */
    private Boolean redisEnabled;
    
    /**
     * 缓存TTL（秒）
     */
    private Integer cacheTTL;
    
    /**
     * 推荐模型版本
     */
    private String modelVersion;
}

