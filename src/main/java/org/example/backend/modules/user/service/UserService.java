package org.example.backend.modules.user.service;

import org.example.backend.common.PageResult;
import org.example.backend.dto.user.*;
import org.example.backend.vo.user.UserInfoVO;
import org.example.backend.vo.user.BorrowHistoryVO;
import org.example.backend.vo.user.FavoriteVO;
import org.example.backend.vo.user.BooklistVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取当前用户信息
     */
    UserInfoVO getCurrentUserInfo(Long userId);
    
    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, UserUpdateDTO updateDTO);
    
    /**
     * 获取借阅历史
     */
    PageResult<BorrowHistoryVO> getBorrowHistory(Long userId, Long page, Long size);
    
    /**
     * 获取收藏列表
     */
    PageResult<FavoriteVO> getFavoriteList(Long userId, Long page, Long size);
    
    // ========== 书单管理 ==========
    
    /**
     * 创建书单
     */
    Long createBooklist(Long userId, BooklistDTO booklistDTO);
    
    /**
     * 更新书单
     */
    void updateBooklist(Long userId, Long booklistId, BooklistDTO booklistDTO);
    
    /**
     * 删除书单
     */
    void deleteBooklist(Long userId, Long booklistId);
    
    /**
     * 获取书单列表
     */
    PageResult<BooklistVO> getBooklistList(Long userId, Long page, Long size);
    
    /**
     * 获取书单详情
     */
    BooklistVO getBooklistDetail(Long userId, Long booklistId);
    
    /**
     * 添加图书到书单
     */
    void addBookToBooklist(Long userId, Long booklistId, Long bookId);
    
    /**
     * 从书单移除图书
     */
    void removeBookFromBooklist(Long userId, Long booklistId, Long bookId);
    
    // ========== 兴趣偏好管理 ==========
    
    /**
     * 设置兴趣偏好
     */
    void setPreference(Long userId, PreferenceDTO preferenceDTO);
    
    /**
     * 获取兴趣偏好
     */
    List<org.example.backend.entity.UserPreference> getPreferences(Long userId);
    
    /**
     * 完成兴趣引导
     */
    void completeInterestGuide(Long userId, InterestGuideDTO guideDTO);
    
    /**
     * 检查是否完成兴趣引导
     */
    boolean isInterestGuideCompleted(Long userId);
    
    // ========== 浏览记录 ==========
    
    /**
     * 记录图书浏览
     */
    void recordBookView(Long userId, Long bookId);
}


