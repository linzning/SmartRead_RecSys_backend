package org.example.backend.modules.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.common.ApiResponse;
import org.example.backend.common.PageResult;
import org.example.backend.common.util.UserContext;
import org.example.backend.dto.user.*;
import org.example.backend.entity.UserPreference;
import org.example.backend.modules.user.service.UserService;
import org.example.backend.vo.user.BorrowHistoryVO;
import org.example.backend.vo.user.FavoriteVO;
import org.example.backend.vo.user.UserInfoVO;
import org.example.backend.vo.user.BooklistVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户信息、借阅历史、收藏、书单等接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public ApiResponse<UserInfoVO> getCurrentUserInfo() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        UserInfoVO userInfo = userService.getCurrentUserInfo(userId);
        return ApiResponse.success(userInfo);
    }
    
    @PutMapping("/info")
    @Operation(summary = "更新用户信息")
    public ApiResponse<Void> updateUserInfo(@Valid @RequestBody UserUpdateDTO updateDTO) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        userService.updateUserInfo(userId, updateDTO);
        return ApiResponse.<Void>success("更新成功", null);
    }
    
    @GetMapping("/borrow-history")
    @Operation(summary = "获取借阅历史")
    public ApiResponse<PageResult<BorrowHistoryVO>> getBorrowHistory(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        PageResult<BorrowHistoryVO> result = userService.getBorrowHistory(userId, page, size);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/favorites")
    @Operation(summary = "获取收藏列表")
    public ApiResponse<PageResult<FavoriteVO>> getFavoriteList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        PageResult<FavoriteVO> result = userService.getFavoriteList(userId, page, size);
        return ApiResponse.success(result);
    }
    
    // ========== 书单管理 ==========
    
    @PostMapping("/booklists")
    @Operation(summary = "创建书单")
    public ApiResponse<Long> createBooklist(@Valid @RequestBody BooklistDTO booklistDTO) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        Long booklistId = userService.createBooklist(userId, booklistDTO);
        return ApiResponse.success(booklistId);
    }
    
    @PutMapping("/booklists/{booklistId}")
    @Operation(summary = "更新书单")
    public ApiResponse<Void> updateBooklist(@PathVariable Long booklistId, @Valid @RequestBody BooklistDTO booklistDTO) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        userService.updateBooklist(userId, booklistId, booklistDTO);
        return ApiResponse.<Void>success("更新成功", null);
    }
    
    @DeleteMapping("/booklists/{booklistId}")
    @Operation(summary = "删除书单")
    public ApiResponse<Void> deleteBooklist(@PathVariable Long booklistId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        userService.deleteBooklist(userId, booklistId);
        return ApiResponse.<Void>success("删除成功", null);
    }
    
    @GetMapping("/booklists")
    @Operation(summary = "获取书单列表")
    public ApiResponse<PageResult<BooklistVO>> getBooklistList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        PageResult<BooklistVO> result = userService.getBooklistList(userId, page, size);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/booklists/{booklistId}")
    @Operation(summary = "获取书单详情")
    public ApiResponse<BooklistVO> getBooklistDetail(@PathVariable Long booklistId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        BooklistVO result = userService.getBooklistDetail(userId, booklistId);
        return ApiResponse.success(result);
    }
    
    @PostMapping("/booklists/{booklistId}/books/{bookId}")
    @Operation(summary = "添加图书到书单")
    public ApiResponse<Void> addBookToBooklist(@PathVariable Long booklistId, @PathVariable Long bookId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        userService.addBookToBooklist(userId, booklistId, bookId);
        return ApiResponse.<Void>success("添加成功", null);
    }
    
    @DeleteMapping("/booklists/{booklistId}/books/{bookId}")
    @Operation(summary = "从书单移除图书")
    public ApiResponse<Void> removeBookFromBooklist(@PathVariable Long booklistId, @PathVariable Long bookId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        userService.removeBookFromBooklist(userId, booklistId, bookId);
        return ApiResponse.<Void>success("移除成功", null);
    }
    
    // ========== 兴趣偏好管理 ==========
    
    @PostMapping("/preferences")
    @Operation(summary = "设置兴趣偏好")
    public ApiResponse<Void> setPreference(@Valid @RequestBody PreferenceDTO preferenceDTO) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        userService.setPreference(userId, preferenceDTO);
        return ApiResponse.<Void>success("设置成功", null);
    }
    
    @GetMapping("/preferences")
    @Operation(summary = "获取兴趣偏好")
    public ApiResponse<List<UserPreference>> getPreferences() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        List<UserPreference> preferences = userService.getPreferences(userId);
        return ApiResponse.success(preferences);
    }
    
    @PostMapping("/interest-guide")
    @Operation(summary = "完成兴趣引导")
    public ApiResponse<Void> completeInterestGuide(@Valid @RequestBody InterestGuideDTO guideDTO) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        userService.completeInterestGuide(userId, guideDTO);
        return ApiResponse.<Void>success("引导完成", null);
    }
    
    @GetMapping("/interest-guide/status")
    @Operation(summary = "检查是否完成兴趣引导")
    public ApiResponse<Boolean> isInterestGuideCompleted() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.success(false);
        }
        boolean completed = userService.isInterestGuideCompleted(userId);
        return ApiResponse.success(completed);
    }
    
    // ========== 浏览记录 ==========
    
    @PostMapping("/book-views/{bookId}")
    @Operation(summary = "记录图书浏览")
    public ApiResponse<Void> recordBookView(@PathVariable Long bookId) {
        Long userId = UserContext.getCurrentUserId();
        userService.recordBookView(userId, bookId);
        return ApiResponse.<Void>success("记录成功", null);
    }
}


