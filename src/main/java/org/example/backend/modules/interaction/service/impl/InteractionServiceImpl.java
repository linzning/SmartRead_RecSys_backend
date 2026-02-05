package org.example.backend.modules.interaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.common.PageResult;
import org.example.backend.common.exception.BusinessException;
import org.example.backend.dto.interaction.CommentDTO;
import org.example.backend.dto.interaction.RatingDTO;
import org.example.backend.entity.*;
import org.example.backend.modules.book.repository.BookMapper;
import org.example.backend.modules.admin.repository.UserBehaviorMapper;
import org.example.backend.modules.interaction.repository.BorrowRecordMapper;
import org.example.backend.modules.interaction.repository.CommentMapper;
import org.example.backend.modules.interaction.repository.FavoriteMapper;
import org.example.backend.modules.interaction.repository.RatingMapper;
import org.example.backend.modules.interaction.service.InteractionService;
import org.example.backend.modules.user.repository.UserMapper;
import org.example.backend.vo.interaction.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 互动服务实现
 */
@Service
public class InteractionServiceImpl implements InteractionService {
    
    @Autowired
    private RatingMapper ratingMapper;
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    
    @Override
    @Transactional
    public void rate(RatingDTO ratingDTO, Long userId) {
        Book book = bookMapper.selectById(ratingDTO.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        // 查询是否已评分
        Rating existRating = ratingMapper.selectOne(
                new LambdaQueryWrapper<Rating>()
                        .eq(Rating::getUserId, userId)
                        .eq(Rating::getBookId, ratingDTO.getBookId())
        );
        
        if (existRating != null) {
            // 更新评分
            existRating.setScore(ratingDTO.getScore());
            existRating.setUpdateTime(LocalDateTime.now());
            ratingMapper.updateById(existRating);
        } else {
            // 新增评分
            Rating rating = new Rating();
            rating.setUserId(userId);
            rating.setBookId(ratingDTO.getBookId());
            rating.setScore(ratingDTO.getScore());
            rating.setCreateTime(LocalDateTime.now());
            rating.setUpdateTime(LocalDateTime.now());
            ratingMapper.insert(rating);
        }
        
        // 更新图书平均分
        updateBookAvgRating(ratingDTO.getBookId());

        // 记录行为日志（评分）
        logBehavior(userId, ratingDTO.getBookId(), "RATING", "{\"score\":" + ratingDTO.getScore() + "}");
    }
    
    @Override
    @Transactional
    public void toggleFavorite(Long bookId, Long userId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        Favorite favorite = favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getBookId, bookId)
        );
        
        if (favorite != null) {
            // 取消收藏
            favoriteMapper.deleteById(favorite.getId());
            // 更新收藏数
            book.setFavoriteCount(Math.max(0, book.getFavoriteCount() - 1));

            // 记录行为日志（取消收藏）
            logBehavior(userId, bookId, "FAVORITE", "{\"action\":\"REMOVE\"}");
        } else {
            // 添加收藏
            Favorite newFavorite = new Favorite();
            newFavorite.setUserId(userId);
            newFavorite.setBookId(bookId);
            newFavorite.setCreateTime(LocalDateTime.now());
            favoriteMapper.insert(newFavorite);
            // 更新收藏数
            book.setFavoriteCount(book.getFavoriteCount() + 1);

            // 记录行为日志（收藏）
            logBehavior(userId, bookId, "FAVORITE", "{\"action\":\"ADD\"}");
        }
        
        bookMapper.updateById(book);
    }
    
    @Override
    @Transactional
    public void addComment(CommentDTO commentDTO, Long userId) {
        Book book = bookMapper.selectById(commentDTO.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setBookId(commentDTO.getBookId());
        comment.setRating(commentDTO.getRating());
        comment.setContent(commentDTO.getContent());
        comment.setStatus(0); // 0-待审核
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        
        commentMapper.insert(comment);
    }
    
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        // 只能删除自己的评论
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }
        
        commentMapper.deleteById(commentId);
    }
    
    @Override
    public PageResult<CommentVO> listComments(Long bookId, Long page, Long size) {
        Page<Comment> commentPage = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getBookId, bookId);
        wrapper.eq(Comment::getStatus, 1); // 只显示已审核通过的评论
        wrapper.orderByDesc(Comment::getCreateTime);
        
        Page<Comment> result = commentMapper.selectPage(commentPage, wrapper);
        
        List<CommentVO> commentVOList = result.getRecords().stream().map(comment -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(comment, vo);
            
            // 查询用户信息
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                vo.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
                vo.setAvatar(user.getAvatar());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), commentVOList);
    }
    
    /**
     * 更新图书平均分
     */
    private void updateBookAvgRating(Long bookId) {
        List<Rating> ratings = ratingMapper.selectList(
                new LambdaQueryWrapper<Rating>().eq(Rating::getBookId, bookId)
        );
        
        if (ratings.isEmpty()) {
            return;
        }
        
        double avgScore = ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
        
        Book book = bookMapper.selectById(bookId);
        book.setAvgRating(BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP));
        book.setRatingCount(ratings.size());
        bookMapper.updateById(book);
    }
    
    @Override
    @Transactional
    public void borrowBook(Long bookId, Long userId, String expectedReturnTime) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }

        if (expectedReturnTime == null || expectedReturnTime.trim().isEmpty()) {
            throw new BusinessException("请选择预计归还时间");
        }
        LocalDateTime expectedReturnAt = parseExpectedReturnTime(expectedReturnTime.trim());
        if (expectedReturnAt.isBefore(LocalDateTime.now())) {
            throw new BusinessException("预计归还时间不能早于当前时间");
        }
        
        // 检查是否已存在“借阅中”或“待审核”的记录
        BorrowRecord existRecord = borrowRecordMapper.selectOne(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getUserId, userId)
                        .eq(BorrowRecord::getBookId, bookId)
                        .and(w -> w.eq(BorrowRecord::getStatus, 1)
                                .or().eq(BorrowRecord::getAuditStatus, 0)) // 0表示待审核
        );
        
        if (existRecord != null) {
            throw new BusinessException("您已提交借阅申请或正在借阅中，请勿重复申请");
        }
        
        // 创建借阅申请（等待管理员审核）
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setApplyTime(LocalDateTime.now());
        record.setExpectedReturnTime(expectedReturnAt);
        record.setAuditStatus(0); // 待审核
        record.setStatus(0); // 未借出（等待管理员审核通过后变更为借阅中）
        
        borrowRecordMapper.insert(record);

        // 记录行为日志（借阅申请）
        logBehavior(userId, bookId, "BORROW", "{\"stage\":\"APPLY\",\"expectedReturnTime\":\"" + escapeJson(expectedReturnTime.trim()) + "\"}");
    }

    private void logBehavior(Long userId, Long bookId, String type, String dataJson) {
        // user_behaviors.user_id 是 NOT NULL：未登录/无userId不记录
        if (userId == null || bookId == null) {
            return;
        }
        try {
            UserBehavior ub = new UserBehavior();
            ub.setUserId(userId);
            ub.setBookId(bookId);
            ub.setBehaviorType(type);
            ub.setBehaviorData(dataJson);
            ub.setCreateTime(LocalDateTime.now());
            userBehaviorMapper.insert(ub);
        } catch (Exception ignored) {
            // 行为日志失败不影响主流程
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private LocalDateTime parseExpectedReturnTime(String raw) {
        // 允许两种：yyyy-MM-dd（默认当天23:59:59）或 yyyy-MM-dd HH:mm:ss 或 ISO8601
        try {
            if (raw.length() == 10 && raw.matches("\\d{4}-\\d{2}-\\d{2}")) {
                LocalDate d = LocalDate.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE);
                return d.atTime(23, 59, 59);
            }
        } catch (Exception ignored) {
            // fallthrough
        }
        try {
            // 兼容 2026-01-27T23:59:59
            String normalized = raw.replace('T', ' ');
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(normalized, fmt);
        } catch (Exception e) {
            throw new BusinessException("预计归还时间格式不正确");
        }
    }
    
    @Override
    @Transactional
    public void returnBook(Long borrowId, Long userId) {
        BorrowRecord record = borrowRecordMapper.selectById(borrowId);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        
        // 只能操作自己的借阅
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权归还此借阅");
        }
        
        if (record.getAuditStatus() == null || record.getAuditStatus() != 1) {
            throw new BusinessException("借阅未审核通过，无法申请归还");
        }
        
        if (record.getStatus() == 0) {
            throw new BusinessException("当前借阅未处于借阅中状态");
        }
        
        if (record.getReturnApplyTime() != null) {
            throw new BusinessException("已提交还书申请，请等待管理员确认");
        }
        
        // 用户发起“还书申请”，等待管理员确认
        record.setReturnApplyTime(LocalDateTime.now());
        
        borrowRecordMapper.updateById(record);
    }
}

