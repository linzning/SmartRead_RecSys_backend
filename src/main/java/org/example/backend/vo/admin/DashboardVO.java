package org.example.backend.vo.admin;

import lombok.Data;

/**
 * 仪表盘VO
 */
@Data
public class DashboardVO {
    
    /**
     * 馆藏周转率
     */
    private Double turnoverRate;
    
    /**
     * 推荐点击率
     */
    private Double recommendCTR;
    
    /**
     * 活跃用户数（DAU）
     */
    private Long activeUsers;
    
    /**
     * 系统告警数
     */
    private Integer alertCount;
}

