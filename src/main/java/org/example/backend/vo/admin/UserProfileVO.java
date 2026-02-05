package org.example.backend.vo.admin;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 用户画像VO
 */
@Data
public class UserProfileVO {
    
    private Long userId;
    private String username;
    private List<String> preferredTopics; // 偏好主题
    private List<String> preferredAuthors; // 偏好作者
    private Map<String, Object> activeBehavior; // 活跃行为
    private Map<String, Object> last30DaysStats; // 最近30天统计
}

