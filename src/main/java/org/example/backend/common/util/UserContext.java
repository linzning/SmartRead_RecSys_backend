package org.example.backend.common.util;

import org.example.backend.common.constants.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 */
public class UserContext {
    
    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }
    
    /**
     * 从请求中获取用户ID
     */
    public static Long getUserIdFromRequest(HttpServletRequest request) {
        Object userId = request.getAttribute(Constants.USER_ID_HEADER);
        if (userId != null) {
            return Long.valueOf(userId.toString());
        }
        return null;
    }
    
    /**
     * 获取当前用户角色
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null 
                && !authentication.getAuthorities().isEmpty()) {
            String authority = authentication.getAuthorities().iterator().next().getAuthority();
            return authority.replace("ROLE_", "");
        }
        return null;
    }
}

