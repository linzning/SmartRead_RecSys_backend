package org.example.backend.modules.interaction.service;

import org.example.backend.common.PageResult;
import org.example.backend.dto.interaction.CommentDTO;
import org.example.backend.dto.interaction.RatingDTO;
import org.example.backend.vo.interaction.CommentVO;

/**
 * 互动服务接口
 */
public interface InteractionService {
    
    /**
     * 评分
     */
    void rate(RatingDTO ratingDTO, Long userId);
    
    /**
     * 收藏/取消收藏
     */
    void toggleFavorite(Long bookId, Long userId);
    
    /**
     * 发表评论
     */
    void addComment(CommentDTO commentDTO, Long userId);
    
    /**
     * 删除评论
     */
    void deleteComment(Long commentId, Long userId);
    
    /**
     * 分页查询评论
     */
    PageResult<CommentVO> listComments(Long bookId, Long page, Long size);
    
    /**
     * 借阅图书
     */
    void borrowBook(Long bookId, Long userId, String expectedReturnTime);
    
    /**
     * 归还图书
     */
    void returnBook(Long borrowId, Long userId);
}

