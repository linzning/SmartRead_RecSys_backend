package org.example.backend.modules.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.common.ApiResponse;
import org.example.backend.common.PageResult;
import org.example.backend.dto.admin.BlacklistDTO;
import org.example.backend.dto.admin.BookDTO;
import org.example.backend.dto.admin.ColdStartDTO;
import org.example.backend.modules.admin.service.AdminService;
import org.example.backend.vo.admin.*;
import java.util.Map;
import org.example.backend.vo.recommend.RecommendBookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员管理", description = "管理员功能接口")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    // ========== 资源管理 ==========
    
    @PostMapping("/books")
    @Operation(summary = "新增图书")
    public ApiResponse<Void> addBook(@Valid @RequestBody BookDTO bookDTO) {
        adminService.addBook(bookDTO);
        return ApiResponse.<Void>success("新增成功", null);
    }
    
    @PutMapping("/books/{bookId}")
    @Operation(summary = "更新图书")
    public ApiResponse<Void> updateBook(@PathVariable Long bookId, @Valid @RequestBody BookDTO bookDTO) {
        adminService.updateBook(bookId, bookDTO);
        return ApiResponse.<Void>success("更新成功", null);
    }
    
    @DeleteMapping("/books/{bookId}")
    @Operation(summary = "删除图书")
    public ApiResponse<Void> deleteBook(@PathVariable Long bookId) {
        adminService.deleteBook(bookId);
        return ApiResponse.<Void>success("删除成功", null);
    }
    
    @PutMapping("/books/{bookId}/status")
    @Operation(summary = "上下架图书")
    public ApiResponse<Void> toggleBookStatus(@PathVariable Long bookId, @RequestParam Integer status) {
        adminService.toggleBookStatus(bookId, status);
        return ApiResponse.<Void>success("操作成功", null);
    }
    
    // ========== 用户管理 ==========
    
    @GetMapping("/users")
    @Operation(summary = "分页查询用户")
    public ApiResponse<PageResult<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<UserVO> result = adminService.listUsers(page, size, keyword, status);
        return ApiResponse.success(result);
    }
    
    @PutMapping("/users/{userId}/status")
    @Operation(summary = "启用/禁用用户")
    public ApiResponse<Void> toggleUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        adminService.toggleUserStatus(userId, status);
        return ApiResponse.<Void>success("操作成功", null);
    }

    @PostMapping("/users")
    @Operation(summary = "管理员创建用户")
    public ApiResponse<Void> createUser(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");
        adminService.createUser(username, email, password);
        return ApiResponse.<Void>success("创建成功", null);
    }
    
    @GetMapping("/users/{userId}/profile")
    @Operation(summary = "获取用户画像")
    public ApiResponse<UserProfileVO> getUserProfile(@PathVariable Long userId) {
        UserProfileVO profile = adminService.getUserProfile(userId);
        return ApiResponse.success(profile);
    }
    
    // ========== 系统管理 ==========
    
    @GetMapping("/dashboard")
    @Operation(summary = "获取仪表盘数据")
    public ApiResponse<DashboardVO> getDashboard() {
        DashboardVO dashboard = adminService.getDashboard();
        return ApiResponse.success(dashboard);
    }
    
    @GetMapping("/system/config")
    @Operation(summary = "获取系统配置")
    public ApiResponse<SystemConfigVO> getSystemConfig() {
        SystemConfigVO config = adminService.getSystemConfig();
        return ApiResponse.success(config);
    }
    
    @PutMapping("/system/config")
    @Operation(summary = "更新系统配置")
    public ApiResponse<Void> updateSystemConfig(@RequestBody SystemConfigVO config) {
        adminService.updateSystemConfig(config);
        return ApiResponse.<Void>success("更新成功", null);
    }
    
    // ========== 评论管理 ==========
    
    @GetMapping("/comments")
    @Operation(summary = "分页查询评论（支持按状态筛选和关键字搜索）")
    public ApiResponse<PageResult<org.example.backend.vo.interaction.CommentVO>> listComments(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        PageResult<org.example.backend.vo.interaction.CommentVO> result = adminService.listComments(page, size, status, keyword);
        return ApiResponse.success(result);
    }
    
    @PutMapping("/comments/{commentId}/audit")
    @Operation(summary = "审核评论")
    public ApiResponse<Void> auditComment(
            @PathVariable Long commentId,
            @RequestParam Integer status) {
        adminService.auditComment(commentId, status);
        return ApiResponse.<Void>success("审核成功", null);
    }
    
    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "删除评论")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        adminService.deleteComment(commentId);
        return ApiResponse.<Void>success("删除成功", null);
    }
    
    // ========== 推荐策略管理 ==========
    
    @GetMapping("/recommend/strategy")
    @Operation(summary = "获取推荐策略配置")
    public ApiResponse<RecommendStrategyVO> getRecommendStrategy() {
        RecommendStrategyVO strategy = adminService.getRecommendStrategy();
        return ApiResponse.success(strategy);
    }
    
    @PutMapping("/recommend/strategy")
    @Operation(summary = "更新推荐策略配置")
    public ApiResponse<Void> updateRecommendStrategy(@RequestBody RecommendStrategyVO strategy) {
        adminService.updateRecommendStrategy(strategy);
        return ApiResponse.<Void>success("更新成功", null);
    }

    // ========== 推荐运营管理 ==========

    @GetMapping("/recommend/preview")
    @Operation(summary = "推荐结果预览")
    public ApiResponse<List<RecommendBookVO>> previewRecommendations(
            @RequestParam(required = false) String recommendType,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Double personalizationWeight,
            @RequestParam(required = false) Double diversityWeight
    ) {
        List<RecommendBookVO> list = adminService.previewRecommendations(recommendType, limit, personalizationWeight, diversityWeight);
        return ApiResponse.success(list);
    }

    @GetMapping("/recommend/blacklist")
    @Operation(summary = "推荐黑名单列表")
    public ApiResponse<PageResult<org.example.backend.entity.RecommendBlacklist>> listRecommendBlacklist(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String recommendType,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Long bookId
    ) {
        return ApiResponse.success(adminService.listRecommendBlacklist(page, size, recommendType, position, bookId));
    }

    @PostMapping("/recommend/blacklist")
    @Operation(summary = "新增/更新推荐黑名单")
    public ApiResponse<Void> upsertRecommendBlacklist(@Valid @RequestBody BlacklistDTO dto) {
        adminService.upsertRecommendBlacklist(dto);
        return ApiResponse.<Void>success("保存成功", null);
    }

    @DeleteMapping("/recommend/blacklist/{id}")
    @Operation(summary = "删除推荐黑名单")
    public ApiResponse<Void> deleteRecommendBlacklist(@PathVariable Long id) {
        adminService.deleteRecommendBlacklist(id);
        return ApiResponse.<Void>success("删除成功", null);
    }

    @GetMapping("/recommend/cold-start")
    @Operation(summary = "冷启动管理列表")
    public ApiResponse<PageResult<org.example.backend.entity.BookColdStart>> listColdStart(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer isEnabled
    ) {
        return ApiResponse.success(adminService.listColdStart(page, size, bookId, isEnabled));
    }

    @PostMapping("/recommend/cold-start")
    @Operation(summary = "新增/更新冷启动配置")
    public ApiResponse<Void> upsertColdStart(@Valid @RequestBody ColdStartDTO dto) {
        adminService.upsertColdStart(dto);
        return ApiResponse.<Void>success("保存成功", null);
    }

    @DeleteMapping("/recommend/cold-start/{id}")
    @Operation(summary = "删除冷启动配置")
    public ApiResponse<Void> deleteColdStart(@PathVariable Long id) {
        adminService.deleteColdStart(id);
        return ApiResponse.<Void>success("删除成功", null);
    }

    // ========== 数据统计分析 ==========

    @GetMapping("/stats/user-growth")
    @Operation(summary = "用户增长统计（按天）")
    public ApiResponse<List<StatPointVO>> userGrowth(@RequestParam(required = false) Integer days) {
        return ApiResponse.success(adminService.userGrowth(days));
    }

    @GetMapping("/stats/book-rank")
    @Operation(summary = "图书热度排行（click/favorite）")
    public ApiResponse<List<BookRankVO>> bookRank(
            @RequestParam(required = false) String metric,
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) Integer limit
    ) {
        return ApiResponse.success(adminService.bookRank(metric, days, limit));
    }

    @GetMapping("/stats/recommend-funnel")
    @Operation(summary = "推荐转化漏斗（点击→收藏→评分）")
    public ApiResponse<RecommendFunnelVO> recommendFunnel(
            @RequestParam(required = false) String recommendType,
            @RequestParam(required = false) Integer days
    ) {
        return ApiResponse.success(adminService.recommendFunnel(recommendType, days));
    }
    
    // ========== 借阅管理 ==========
    
    @GetMapping("/borrows")
    @Operation(summary = "分页查询借阅记录")
    public ApiResponse<PageResult<BorrowRecordVO>> listBorrowRecords(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) String keyword) {
        PageResult<BorrowRecordVO> result = adminService.listBorrowRecords(page, size, status, auditStatus, keyword);
        return ApiResponse.success(result);
    }
    
    @PutMapping("/borrows/{borrowId}/audit")
    @Operation(summary = "审核借阅申请")
    public ApiResponse<Void> auditBorrowRecord(
            @PathVariable Long borrowId,
            @RequestParam Integer auditStatus) {
        adminService.auditBorrowRecord(borrowId, auditStatus);
        return ApiResponse.<Void>success("审核成功", null);
    }
    
    @PutMapping("/borrows/{borrowId}/return")
    @Operation(summary = "确认还书")
    public ApiResponse<Void> confirmReturn(@PathVariable Long borrowId) {
        adminService.confirmReturn(borrowId);
        return ApiResponse.<Void>success("还书确认成功", null);
    }
    
    @GetMapping("/borrows/overdue")
    @Operation(summary = "查询逾期记录")
    public ApiResponse<PageResult<BorrowRecordVO>> getOverdueRecords(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<BorrowRecordVO> result = adminService.getOverdueRecords(page, size);
        return ApiResponse.success(result);
    }
    
    // ========== 行为日志管理 ==========
    
    @GetMapping("/behaviors")
    @Operation(summary = "分页查询用户行为日志")
    public ApiResponse<PageResult<UserBehaviorVO>> listUserBehaviors(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) String keyword) {
        PageResult<UserBehaviorVO> result = adminService.listUserBehaviors(page, size, userId, behaviorType, keyword);
        return ApiResponse.success(result);
    }
}

