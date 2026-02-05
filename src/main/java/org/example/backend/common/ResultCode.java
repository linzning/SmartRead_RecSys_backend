package org.example.backend.common;

/**
 * 响应码常量
 */
public interface ResultCode {
    
    /**
     * 成功
     */
    int SUCCESS = 200;
    
    /**
     * 参数错误
     */
    int BAD_REQUEST = 400;
    
    /**
     * 未授权
     */
    int UNAUTHORIZED = 401;
    
    /**
     * 禁止访问
     */
    int FORBIDDEN = 403;
    
    /**
     * 资源不存在
     */
    int NOT_FOUND = 404;
    
    /**
     * 服务器错误
     */
    int INTERNAL_ERROR = 500;
    
    /**
     * 业务错误
     */
    int BUSINESS_ERROR = 600;
}

