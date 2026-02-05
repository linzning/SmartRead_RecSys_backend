package org.example.backend.common.constants;

/**
 * 系统常量
 */
public class Constants {
    
    /**
     * JWT Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * JWT Token Header 名称
     */
    public static final String TOKEN_HEADER = "Authorization";
    
    /**
     * 用户ID请求头
     */
    public static final String USER_ID_HEADER = "X-User-Id";
    
    /**
     * 默认分页大小
     */
    public static final Long DEFAULT_PAGE_SIZE = 10L;
    
    /**
     * 最大分页大小
     */
    public static final Long MAX_PAGE_SIZE = 100L;
    
    /**
     * 角色：管理员
     */
    public static final String ROLE_ADMIN = "ADMIN";
    
    /**
     * 角色：普通用户
     */
    public static final String ROLE_USER = "USER";
    
    /**
     * 用户状态：启用
     */
    public static final Integer USER_STATUS_ENABLED = 1;
    
    /**
     * 用户状态：禁用
     */
    public static final Integer USER_STATUS_DISABLED = 0;
    
    /**
     * 图书状态：上架
     */
    public static final Integer BOOK_STATUS_ON_SALE = 1;
    
    /**
     * 图书状态：下架
     */
    public static final Integer BOOK_STATUS_OFF_SALE = 0;
    
    /**
     * 搜索模式：关键词
     */
    public static final String SEARCH_MODE_KEYWORD = "keyword";
    
    /**
     * 搜索模式：语义
     */
    public static final String SEARCH_MODE_SEMANTIC = "semantic";
    
    /**
     * 搜索模式：混合
     */
    public static final String SEARCH_MODE_HYBRID = "hybrid";
}

