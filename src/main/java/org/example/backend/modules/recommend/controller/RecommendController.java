package org.example.backend.modules.recommend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.common.ApiResponse;
import org.example.backend.common.util.UserContext;
import org.example.backend.modules.recommend.service.RecommendService;
import org.example.backend.vo.book.BookVO;
import org.example.backend.vo.recommend.RecommendBookVO;
import org.example.backend.vo.recommend.TopicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制器
 */
@RestController
@RequestMapping("/api/recommend")
@Tag(name = "推荐管理", description = "个性化推荐接口")
public class RecommendController {
    
    @Autowired
    private RecommendService recommendService;
    
    @GetMapping("/home")
    @Operation(summary = "首页推荐（猜你喜欢）")
    public ApiResponse<List<RecommendBookVO>> getHomeRecommendations(
            @RequestParam(required = false) Double personalizationWeight,
            @RequestParam(required = false) Double diversityWeight) {
        Long userId = UserContext.getCurrentUserId();
        if (personalizationWeight == null) personalizationWeight = 0.7;
        if (diversityWeight == null) diversityWeight = 0.3;
        
        List<RecommendBookVO> recommendations = recommendService.getHomeRecommendations(
                userId, personalizationWeight, diversityWeight);
        return ApiResponse.success(recommendations);
    }
    
    @GetMapping("/new")
    @Operation(summary = "新书抢先看")
    public ApiResponse<List<BookVO>> getNewBooks(@RequestParam(defaultValue = "10") int limit) {
        List<BookVO> books = recommendService.getNewBooks(limit);
        return ApiResponse.success(books);
    }
    
    @GetMapping("/hot")
    @Operation(summary = "热门排行榜")
    public ApiResponse<List<BookVO>> getHotBooks(@RequestParam(defaultValue = "10") int limit) {
        List<BookVO> books = recommendService.getHotBooks(limit);
        return ApiResponse.success(books);
    }
    
    @GetMapping("/topic")
    @Operation(summary = "主题漫游")
    public ApiResponse<List<BookVO>> getBooksByTopic(
            @RequestParam String topic,
            @RequestParam(defaultValue = "10") int limit) {
        List<BookVO> books = recommendService.getBooksByTopic(topic, limit);
        return ApiResponse.success(books);
    }
    
    @GetMapping("/similar/{bookId}")
    @Operation(summary = "相似图书推荐")
    public ApiResponse<List<RecommendBookVO>> getSimilarBooks(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "5") int limit) {
        List<RecommendBookVO> books = recommendService.getSimilarBooks(bookId, limit);
        return ApiResponse.success(books);
    }
    
    @GetMapping("/user-also-read/{bookId}")
    @Operation(summary = "阅读此书的用户也读")
    public ApiResponse<List<RecommendBookVO>> getUserAlsoRead(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "5") int limit) {
        List<RecommendBookVO> books = recommendService.getUserAlsoRead(bookId, limit);
        return ApiResponse.success(books);
    }
    
    @PostMapping("/exposure")
    @Operation(summary = "记录推荐曝光")
    public ApiResponse<Void> recordExposure(
            @RequestParam Long bookId,
            @RequestParam String recommendType,
            @RequestParam String position) {
        Long userId = UserContext.getCurrentUserId();
        recommendService.recordExposure(userId, bookId, recommendType, position);
        return ApiResponse.success();
    }
    
    @PostMapping("/click")
    @Operation(summary = "记录推荐点击")
    public ApiResponse<Void> recordClick(
            @RequestParam Long bookId,
            @RequestParam String recommendType,
            @RequestParam String position) {
        Long userId = UserContext.getCurrentUserId();
        recommendService.recordClick(userId, bookId, recommendType, position);
        return ApiResponse.success();
    }
    
    @PostMapping("/feedback")
    @Operation(summary = "反馈（不感兴趣）")
    public ApiResponse<Void> feedback(
            @RequestParam Long bookId,
            @RequestParam String feedbackType,
            @RequestParam(required = false) String reason) {
        Long userId = UserContext.getCurrentUserId();
        recommendService.feedback(userId, bookId, feedbackType, reason);
        return ApiResponse.<Void>success("反馈已记录", null);
    }
    
    @GetMapping("/topics")
    @Operation(summary = "获取热门主题列表")
    public ApiResponse<List<TopicVO>> getPopularTopics(@RequestParam(defaultValue = "10") int limit) {
        List<TopicVO> topics = recommendService.getPopularTopics(limit);
        return ApiResponse.success(topics);
    }
    
    @GetMapping("/long-tail")
    @Operation(summary = "长尾推荐（冷门佳作）")
    public ApiResponse<List<RecommendBookVO>> getLongTailRecommendations(@RequestParam(defaultValue = "10") int limit) {
        List<RecommendBookVO> books = recommendService.getLongTailRecommendations(limit);
        return ApiResponse.success(books);
    }
}

