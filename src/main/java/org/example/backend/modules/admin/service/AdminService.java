package org.example.backend.modules.admin.service;

import org.example.backend.common.PageResult;
import org.example.backend.dto.admin.BlacklistDTO;
import org.example.backend.dto.admin.BookDTO;
import org.example.backend.dto.admin.ColdStartDTO;
import org.example.backend.vo.admin.*;
import org.example.backend.vo.recommend.RecommendBookVO;

import java.util.List;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    // 资源管理
    void addBook(BookDTO bookDTO);
    void updateBook(Long bookId, BookDTO bookDTO);
    void deleteBook(Long bookId);
    void toggleBookStatus(Long bookId, Integer status);
    
    // 用户管理
    PageResult<UserVO> listUsers(Long page, Long size, String keyword, Integer status);
    void toggleUserStatus(Long userId, Integer status);
    void createUser(String username, String email, String rawPassword);
    UserProfileVO getUserProfile(Long userId);
    
    // 系统管理
    DashboardVO getDashboard();
    SystemConfigVO getSystemConfig();
    void updateSystemConfig(SystemConfigVO config);
    
    // 评论管理
    PageResult<org.example.backend.vo.interaction.CommentVO> listComments(Long page, Long size, Integer status, String keyword);
    void auditComment(Long commentId, Integer status);
    void deleteComment(Long commentId);
    
    // 推荐策略管理
    org.example.backend.vo.admin.RecommendStrategyVO getRecommendStrategy();
    void updateRecommendStrategy(org.example.backend.vo.admin.RecommendStrategyVO strategy);

    // 推荐运营管理
    List<RecommendBookVO> previewRecommendations(String recommendType, Integer limit, Double personalizationWeight, Double diversityWeight);
    PageResult<org.example.backend.entity.RecommendBlacklist> listRecommendBlacklist(Long page, Long size, String recommendType, String position, Long bookId);
    void upsertRecommendBlacklist(BlacklistDTO dto);
    void deleteRecommendBlacklist(Long id);

    PageResult<org.example.backend.entity.BookColdStart> listColdStart(Long page, Long size, Long bookId, Integer isEnabled);
    void upsertColdStart(ColdStartDTO dto);
    void deleteColdStart(Long id);

    // 数据统计分析
    List<StatPointVO> userGrowth(Integer days);
    List<BookRankVO> bookRank(String metric, Integer days, Integer limit);
    RecommendFunnelVO recommendFunnel(String recommendType, Integer days);
    
    // 借阅管理
    PageResult<BorrowRecordVO> listBorrowRecords(Long page, Long size, Integer status, Integer auditStatus, String keyword);
    void auditBorrowRecord(Long borrowId, Integer auditStatus);
    void confirmReturn(Long borrowId);
    PageResult<BorrowRecordVO> getOverdueRecords(Long page, Long size);
    
    // 行为日志管理
    PageResult<UserBehaviorVO> listUserBehaviors(Long page, Long size, Long userId, String behaviorType, String keyword);
}

