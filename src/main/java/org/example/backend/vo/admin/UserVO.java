package org.example.backend.vo.admin;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户VO（管理员）
 */
@Data
public class UserVO {
    
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private Integer status;
    private LocalDateTime createTime;
    private Long borrowCount;
}

