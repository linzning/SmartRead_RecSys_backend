package org.example.backend.vo.user;

import lombok.Data;

/**
 * 用户信息VO
 */
@Data
public class UserInfoVO {
    
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private Long borrowCount;
    private Long favoriteCount;
    private Long commentCount;
}


