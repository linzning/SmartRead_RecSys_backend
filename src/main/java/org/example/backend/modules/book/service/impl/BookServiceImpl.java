package org.example.backend.modules.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.common.PageResult;
import org.example.backend.entity.Book;
import org.example.backend.entity.BookTopic;
import org.example.backend.entity.Favorite;
import org.example.backend.entity.Rating;
import org.example.backend.modules.book.repository.BookMapper;
import org.example.backend.modules.book.repository.BookTopicMapper;
import org.example.backend.modules.book.service.BookService;
import org.example.backend.modules.interaction.repository.FavoriteMapper;
import org.example.backend.modules.interaction.repository.RatingMapper;
import org.example.backend.modules.user.repository.BookViewMapper;
import org.example.backend.entity.BookView;
import org.example.backend.entity.UserBehavior;
import org.example.backend.modules.admin.repository.UserBehaviorMapper;
import org.example.backend.vo.book.BookDetailVO;
import org.example.backend.vo.book.BookVO;
import org.example.backend.common.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书服务实现
 */
@Service
public class BookServiceImpl implements BookService {
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private BookTopicMapper bookTopicMapper;
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private RatingMapper ratingMapper;
    
    @Autowired
    private BookViewMapper bookViewMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    
    @Override
    public PageResult<BookVO> listBooks(Long page, Long size) {
        Page<Book> bookPage = new Page<>(page, size);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, 1); // 只查询上架的图书
        wrapper.orderByDesc(Book::getCreateTime);
        
        Page<Book> result = bookMapper.selectPage(bookPage, wrapper);
        
        List<BookVO> bookVOList = result.getRecords().stream().map(book -> {
            BookVO vo = new BookVO();
            BeanUtils.copyProperties(book, vo);
            // 查询主题
            List<String> topics = bookTopicMapper.selectList(
                    new LambdaQueryWrapper<BookTopic>().eq(BookTopic::getBookId, book.getId())
            ).stream().map(BookTopic::getTopicName).collect(Collectors.toList());
            vo.setTopics(topics);
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), bookVOList);
    }
    
    @Override
    public PageResult<BookVO> filterBooks(String keyword, String topic, String author, 
                                           String publisher, Double minRating, String sortBy,
                                           Integer status, Long page, Long size) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        // 状态筛选逻辑：
        // 1. 如果明确指定了status，则按指定状态筛选
        // 2. 如果status为null，判断调用者角色：
        //    - 管理员：返回所有状态的图书（包括上架和下架）
        //    - 普通用户或未登录：只返回上架的图书（status=1）
        if (status != null) {
            wrapper.eq(Book::getStatus, status);
        } else {
            // status为null时，检查用户角色
            String userRole = UserContext.getCurrentUserRole();
            if (!"ADMIN".equals(userRole)) {
                // 非管理员用户，只返回上架的图书
                wrapper.eq(Book::getStatus, 1);
            }
            // 管理员用户，status为null时不添加状态过滤，返回所有状态的图书
        }
        
        // 关键字搜索（书名、作者、ISBN）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Book::getTitle, keyword.trim())
                    .or().like(Book::getAuthor, keyword.trim())
                    .or().like(Book::getIsbn, keyword.trim()));
        }
        
        // 作者筛选
        if (author != null && !author.trim().isEmpty()) {
            wrapper.like(Book::getAuthor, author.trim());
        }
        
        // 出版社筛选
        if (publisher != null && !publisher.trim().isEmpty()) {
            wrapper.eq(Book::getPublisher, publisher.trim());
        }
        
        // 最低评分筛选
        if (minRating != null && minRating > 0) {
            wrapper.ge(Book::getAvgRating, minRating);
        }
        
        // 如果指定了主题筛选，先查询符合条件的bookId
        List<Long> topicBookIds = null;
        if (topic != null && !topic.trim().isEmpty()) {
            String topicName = topic.trim();
            List<BookTopic> bookTopics = bookTopicMapper.selectList(
                    new LambdaQueryWrapper<BookTopic>().eq(BookTopic::getTopicName, topicName)
            );
            topicBookIds = bookTopics.stream()
                    .map(BookTopic::getBookId)
                    .collect(Collectors.toList());
            
            if (topicBookIds.isEmpty()) {
                // 如果没有符合条件的图书，直接返回空结果
                return PageResult.of(page, size, 0L, Collections.emptyList());
            }
            wrapper.in(Book::getId, topicBookIds);
        }
        
        // 排序
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "borrowCount":
                    wrapper.orderByDesc(Book::getBorrowCount);
                    break;
                case "avgRating":
                    wrapper.orderByDesc(Book::getAvgRating);
                    break;
                case "createTime":
                default:
                    wrapper.orderByDesc(Book::getCreateTime);
                    break;
            }
        } else {
            wrapper.orderByDesc(Book::getCreateTime);
        }
        
        Page<Book> bookPage = new Page<>(page, size);
        Page<Book> result = bookMapper.selectPage(bookPage, wrapper);
        
        List<BookVO> bookVOList = result.getRecords().stream().map(book -> {
            BookVO vo = new BookVO();
            BeanUtils.copyProperties(book, vo);
            // 查询主题
            List<String> topics = bookTopicMapper.selectList(
                    new LambdaQueryWrapper<BookTopic>().eq(BookTopic::getBookId, book.getId())
            ).stream().map(BookTopic::getTopicName).collect(Collectors.toList());
            vo.setTopics(topics);
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), bookVOList);
    }
    
    @Override
    public BookDetailVO getBookDetail(Long bookId, Long userId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new org.example.backend.common.exception.BusinessException("图书不存在");
        }
        
        BookDetailVO vo = new BookDetailVO();
        BeanUtils.copyProperties(book, vo);
        
        // 查询主题
        List<String> topics = bookTopicMapper.selectList(
                new LambdaQueryWrapper<BookTopic>().eq(BookTopic::getBookId, bookId)
        ).stream().map(BookTopic::getTopicName).collect(Collectors.toList());
        vo.setTopics(topics);
        
        // 查询是否已收藏
        if (userId != null) {
            Favorite favorite = favoriteMapper.selectOne(
                    new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getUserId, userId)
                            .eq(Favorite::getBookId, bookId)
            );
            vo.setIsFavorited(favorite != null);
            
            // 查询用户评分
            Rating rating = ratingMapper.selectOne(
                    new LambdaQueryWrapper<Rating>()
                            .eq(Rating::getUserId, userId)
                            .eq(Rating::getBookId, bookId)
            );
            if (rating != null) {
                vo.setUserRating(rating.getScore());
            }
            
            // 记录浏览
            recordBookView(userId, bookId);
        } else {
            // 未登录用户也记录浏览（userId为null）
            recordBookView(null, bookId);
        }
        
        return vo;
    }
    
    /**
     * 记录图书浏览（静默记录，失败不影响主流程）
     */
    private void recordBookView(Long userId, Long bookId) {
        try {
            BookView bookView = new BookView();
            bookView.setUserId(userId);
            bookView.setBookId(bookId);
            bookView.setViewTime(LocalDateTime.now());
            bookView.setDuration(0); // 默认0，前端可以后续更新
            bookViewMapper.insert(bookView);

            // 同步写入 user_behaviors（管理员行为日志数据源）
            if (userId != null) {
                UserBehavior ub = new UserBehavior();
                ub.setUserId(userId);
                ub.setBookId(bookId);
                ub.setBehaviorType("VIEW");
                ub.setBehaviorData("{\"duration\":0}");
                ub.setCreateTime(LocalDateTime.now());
                userBehaviorMapper.insert(ub);
            }
        } catch (Exception e) {
            // 浏览记录失败不影响主流程，只记录日志
            System.err.println("记录浏览失败: " + e.getMessage());
        }
    }
}

