package org.example.backend.modules.recommend.service;

import org.example.backend.vo.book.BookVO;
import org.example.backend.vo.recommend.RecommendBookVO;
import org.example.backend.vo.recommend.TopicVO;

import java.util.List;

/**
 * 推荐服务接口
 */
public interface RecommendService {
    
    /**
     * 猜你喜欢（首页推荐）
     */
    List<RecommendBookVO> getHomeRecommendations(Long userId, Double personalizationWeight, Double diversityWeight);
    
    /**
     * 新书抢先看
     */
    List<BookVO> getNewBooks(int limit);
    
    /**
     * 热门排行榜
     */
    List<BookVO> getHotBooks(int limit);
    
    /**
     * 主题漫游
     */
    List<BookVO> getBooksByTopic(String topic, int limit);
    
    /**
     * 相似图书推荐
     */
    List<RecommendBookVO> getSimilarBooks(Long bookId, int limit);
    
    /**
     * 阅读此书的用户也读
     */
    List<RecommendBookVO> getUserAlsoRead(Long bookId, int limit);
    
    /**
     * 记录推荐曝光
     */
    void recordExposure(Long userId, Long bookId, String recommendType, String position);
    
    /**
     * 记录推荐点击
     */
    void recordClick(Long userId, Long bookId, String recommendType, String position);
    
    /**
     * 反馈（不感兴趣）
     */
    void feedback(Long userId, Long bookId, String feedbackType, String reason);
    
    /**
     * 获取热门主题列表
     */
    List<TopicVO> getPopularTopics(int limit);
    
    /**
     * 长尾推荐（冷门佳作）
     */
    List<RecommendBookVO> getLongTailRecommendations(int limit);
}

