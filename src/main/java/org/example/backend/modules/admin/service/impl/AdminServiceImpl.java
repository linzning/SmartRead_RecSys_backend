package org.example.backend.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.common.PageResult;
import org.example.backend.common.exception.BusinessException;
import org.example.backend.dto.admin.BookDTO;
import org.example.backend.dto.admin.BlacklistDTO;
import org.example.backend.dto.admin.ColdStartDTO;
import org.example.backend.entity.*;
import org.example.backend.modules.admin.repository.SystemConfigMapper;
import org.example.backend.modules.admin.service.AdminService;
import org.example.backend.modules.admin.repository.AdminStatsMapper;
import org.example.backend.modules.book.repository.BookMapper;
import org.example.backend.modules.book.repository.BookTopicMapper;
import org.example.backend.modules.interaction.repository.BorrowRecordMapper;
import org.example.backend.modules.interaction.repository.CommentMapper;
import org.example.backend.modules.interaction.repository.RatingMapper;
import org.example.backend.modules.recommend.repository.RecommendClickMapper;
import org.example.backend.modules.recommend.repository.RecommendExposureMapper;
import org.example.backend.modules.recommend.repository.RecommendStrategyMapper;
import org.example.backend.modules.recommend.repository.RecommendBlacklistMapper;
import org.example.backend.modules.recommend.repository.BookColdStartMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.example.backend.modules.recommend.service.RecommendService;
import org.example.backend.modules.user.repository.UserMapper;
import org.example.backend.modules.admin.repository.UserBehaviorMapper;
import org.example.backend.common.util.UserContext;
import org.example.backend.vo.admin.*;
import org.example.backend.vo.interaction.CommentVO;
import org.example.backend.vo.recommend.RecommendBookVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员服务实现
 */
@Service
public class AdminServiceImpl implements AdminService {
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private BookTopicMapper bookTopicMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    
    @Autowired
    private RatingMapper ratingMapper;
    
    @Autowired
    private RecommendExposureMapper recommendExposureMapper;
    
    @Autowired
    private RecommendClickMapper recommendClickMapper;
    
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private RecommendStrategyMapper recommendStrategyMapper;
    
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private RecommendBlacklistMapper recommendBlacklistMapper;

    @Autowired
    private BookColdStartMapper bookColdStartMapper;

    @Autowired
    private AdminStatsMapper adminStatsMapper;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void addBook(BookDTO bookDTO) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        book.setBorrowCount(0);
        book.setFavoriteCount(0);
        book.setRatingCount(0);
        book.setStatus(bookDTO.getStatus() != null ? bookDTO.getStatus() : 1);
        book.setCreateTime(LocalDateTime.now());
        book.setUpdateTime(LocalDateTime.now());
        
        bookMapper.insert(book);
        
        // 保存主题
        if (bookDTO.getTopics() != null && !bookDTO.getTopics().isEmpty()) {
            for (String topic : bookDTO.getTopics()) {
                BookTopic bookTopic = new BookTopic();
                bookTopic.setBookId(book.getId());
                bookTopic.setTopicName(topic);
                bookTopicMapper.insert(bookTopic);
            }
        }
    }
    
    @Override
    @Transactional
    public void updateBook(Long bookId, BookDTO bookDTO) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        BeanUtils.copyProperties(bookDTO, book, "id");
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.updateById(book);
        
        // 更新主题
        bookTopicMapper.delete(new LambdaQueryWrapper<BookTopic>().eq(BookTopic::getBookId, bookId));
        if (bookDTO.getTopics() != null && !bookDTO.getTopics().isEmpty()) {
            for (String topic : bookDTO.getTopics()) {
                BookTopic bookTopic = new BookTopic();
                bookTopic.setBookId(bookId);
                bookTopic.setTopicName(topic);
                bookTopicMapper.insert(bookTopic);
            }
        }
    }
    
    @Override
    @Transactional
    public void deleteBook(Long bookId) {
        bookMapper.deleteById(bookId);
        bookTopicMapper.delete(new LambdaQueryWrapper<BookTopic>().eq(BookTopic::getBookId, bookId));
    }
    
    @Override
    @Transactional
    public void toggleBookStatus(Long bookId, Integer status) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        book.setStatus(status);
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.updateById(book);
    }
    
    @Override
    public PageResult<UserVO> listUsers(Long page, Long size, String keyword, Integer status) {
        Page<User> userPage = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 关键字搜索（用户名、昵称、邮箱）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword.trim())
                    .or().like(User::getNickname, keyword.trim())
                    .or().like(User::getEmail, keyword.trim()));
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = userMapper.selectPage(userPage, wrapper);
        
        List<UserVO> userVOList = result.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            // 统计借阅数
            Long borrowCount = borrowRecordMapper.selectCount(
                    new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getUserId, user.getId())
            );
            vo.setBorrowCount(borrowCount);
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), userVOList);
    }
    
    @Override
    @Transactional
    public void toggleUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void createUser(String username, String email, String rawPassword) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        // 检查用户名唯一
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username.trim())
        );
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }
    
    @Override
    public UserProfileVO getUserProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(userId);
        vo.setUsername(user.getUsername());
        
        // 获取偏好主题（基于借阅记录）
        List<BorrowRecord> borrowRecords = borrowRecordMapper.selectList(
                new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getUserId, userId)
        );
        
        if (!borrowRecords.isEmpty()) {
            List<Long> bookIds = borrowRecords.stream()
                    .map(BorrowRecord::getBookId)
                    .collect(Collectors.toList());
            
            List<BookTopic> topics = bookTopicMapper.selectList(
                    new LambdaQueryWrapper<BookTopic>().in(BookTopic::getBookId, bookIds)
            );
            
            Map<String, Long> topicCount = topics.stream()
                    .collect(Collectors.groupingBy(BookTopic::getTopicName, Collectors.counting()));
            
            List<String> preferredTopics = topicCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            vo.setPreferredTopics(preferredTopics);
        }
        
        // 获取偏好作者
        List<Book> books = bookMapper.selectBatchIds(
                borrowRecords.stream().map(BorrowRecord::getBookId).collect(Collectors.toList())
        );
        
        Map<String, Long> authorCount = books.stream()
                .collect(Collectors.groupingBy(Book::getAuthor, Collectors.counting()));
        
        List<String> preferredAuthors = authorCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        vo.setPreferredAuthors(preferredAuthors);
        
        // 活跃行为
        Map<String, Object> activeBehavior = new HashMap<>();
        activeBehavior.put("totalBorrows", borrowRecords.size());
        activeBehavior.put("totalRatings", ratingMapper.selectCount(
                new LambdaQueryWrapper<Rating>().eq(Rating::getUserId, userId)
        ));
        vo.setActiveBehavior(activeBehavior);
        
        // 最近30天统计
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Map<String, Object> last30DaysStats = new HashMap<>();
        last30DaysStats.put("borrows", borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getUserId, userId)
                        .ge(BorrowRecord::getBorrowTime, thirtyDaysAgo)
        ));
        last30DaysStats.put("ratings", ratingMapper.selectCount(
                new LambdaQueryWrapper<Rating>()
                        .eq(Rating::getUserId, userId)
                        .ge(Rating::getCreateTime, thirtyDaysAgo)
        ));
        vo.setLast30DaysStats(last30DaysStats);
        
        return vo;
    }
    
    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();
        
        // 馆藏周转率（简化计算：借阅次数/图书总数）
        Long totalBooks = bookMapper.selectCount(null);
        Long totalBorrows = borrowRecordMapper.selectCount(null);
        vo.setTurnoverRate(totalBooks > 0 ? (double) totalBorrows / totalBooks * 100 : 0.0);
        
        // 推荐点击率
        Long exposures = recommendExposureMapper.selectCount(null);
        Long clicks = recommendClickMapper.selectCount(null);
        vo.setRecommendCTR(exposures > 0 ? (double) clicks / exposures * 100 : 0.0);
        
        // 活跃用户数（最近7天有借阅的用户）
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Set<Long> activeUserIds = borrowRecordMapper.selectList(
                new LambdaQueryWrapper<BorrowRecord>().ge(BorrowRecord::getBorrowTime, sevenDaysAgo)
        ).stream().map(BorrowRecord::getUserId).collect(Collectors.toSet());
        vo.setActiveUsers((long) activeUserIds.size());
        
        // 系统告警数（简化：返回0）
        vo.setAlertCount(0);
        
        return vo;
    }
    
    @Override
    public SystemConfigVO getSystemConfig() {
        SystemConfigVO vo = new SystemConfigVO();
        
        // 从数据库读取配置
        SystemConfig redisConfig = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, "redis.enabled")
        );
        vo.setRedisEnabled(redisConfig != null && "true".equals(redisConfig.getConfigValue()));
        
        SystemConfig ttlConfig = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, "cache.ttl")
        );
        vo.setCacheTTL(ttlConfig != null ? Integer.parseInt(ttlConfig.getConfigValue()) : 3600);
        
        SystemConfig modelConfig = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, "model.version")
        );
        vo.setModelVersion(modelConfig != null ? modelConfig.getConfigValue() : "v2.4.1");
        
        return vo;
    }
    
    @Override
    @Transactional
    public void updateSystemConfig(SystemConfigVO config) {
        // 更新Redis开关
        SystemConfig redisConfig = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, "redis.enabled")
        );
        if (redisConfig == null) {
            redisConfig = new SystemConfig();
            redisConfig.setConfigKey("redis.enabled");
        }
        redisConfig.setConfigValue(String.valueOf(config.getRedisEnabled()));
        redisConfig.setUpdateTime(LocalDateTime.now());
        if (redisConfig.getId() == null) {
            systemConfigMapper.insert(redisConfig);
        } else {
            systemConfigMapper.updateById(redisConfig);
        }
        
        // 更新TTL
        SystemConfig ttlConfig = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, "cache.ttl")
        );
        if (ttlConfig == null) {
            ttlConfig = new SystemConfig();
            ttlConfig.setConfigKey("cache.ttl");
        }
        ttlConfig.setConfigValue(String.valueOf(config.getCacheTTL()));
        ttlConfig.setUpdateTime(LocalDateTime.now());
        if (ttlConfig.getId() == null) {
            systemConfigMapper.insert(ttlConfig);
        } else {
            systemConfigMapper.updateById(ttlConfig);
        }
        
        // 更新模型版本
        SystemConfig modelConfig = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, "model.version")
        );
        if (modelConfig == null) {
            modelConfig = new SystemConfig();
            modelConfig.setConfigKey("model.version");
        }
        modelConfig.setConfigValue(config.getModelVersion());
        modelConfig.setUpdateTime(LocalDateTime.now());
        if (modelConfig.getId() == null) {
            systemConfigMapper.insert(modelConfig);
        } else {
            systemConfigMapper.updateById(modelConfig);
        }
    }
    
    // ========== 评论管理 ==========
    
    @Override
    public PageResult<CommentVO> listComments(Long page, Long size, Integer status, String keyword) {
        Page<Comment> commentPage = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        
        // 关键字搜索（评论内容、用户名、图书名）
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordTrim = keyword.trim();
            
            // 先查询符合条件的用户ID
            List<User> matchedUsers = userMapper.selectList(
                    new LambdaQueryWrapper<User>()
                            .like(User::getUsername, keywordTrim)
                            .or().like(User::getNickname, keywordTrim)
            );
            Set<Long> matchedUserIds = matchedUsers.stream().map(User::getId).collect(Collectors.toSet());
            
            // 先查询符合条件的图书ID
            List<Book> matchedBooks = bookMapper.selectList(
                    new LambdaQueryWrapper<Book>()
                            .like(Book::getTitle, keywordTrim)
            );
            Set<Long> matchedBookIds = matchedBooks.stream().map(Book::getId).collect(Collectors.toSet());
            
            // 构建查询条件：评论内容匹配 OR 用户ID匹配 OR 图书ID匹配
            wrapper.and(w -> {
                w.like(Comment::getContent, keywordTrim);
                if (!matchedUserIds.isEmpty()) {
                    w.or().in(Comment::getUserId, matchedUserIds);
                }
                if (!matchedBookIds.isEmpty()) {
                    w.or().in(Comment::getBookId, matchedBookIds);
                }
            });
        }
        
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
            
            // 查询图书信息
            Book book = bookMapper.selectById(comment.getBookId());
            if (book != null) {
                vo.setBookTitle(book.getTitle());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), commentVOList);
    }
    
    @Override
    @Transactional
    public void auditComment(Long commentId, Integer status) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        if (status < 0 || status > 2) {
            throw new BusinessException("审核状态无效：0-待审核，1-已通过，2-已拒绝");
        }
        
        comment.setStatus(status);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);
    }
    
    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        commentMapper.deleteById(commentId);
    }
    
    // ========== 推荐策略管理 ==========
    
    @Override
    public RecommendStrategyVO getRecommendStrategy() {
        RecommendStrategyVO vo = new RecommendStrategyVO();
        
        // 从 recommend_strategy 表读取配置
        RecommendStrategy hotRatioStrategy = recommendStrategyMapper.selectOne(
                new LambdaQueryWrapper<RecommendStrategy>()
                        .eq(RecommendStrategy::getStrategyKey, "hot_recommend_ratio")
                        .eq(RecommendStrategy::getIsEnabled, 1)
        );
        vo.setHotRecommendRatio(hotRatioStrategy != null && hotRatioStrategy.getStrategyValue() != null 
                ? Double.parseDouble(hotRatioStrategy.getStrategyValue()) : 0.3);
        
        RecommendStrategy diversityStrategy = recommendStrategyMapper.selectOne(
                new LambdaQueryWrapper<RecommendStrategy>()
                        .eq(RecommendStrategy::getStrategyKey, "global_diversity_weight")
                        .eq(RecommendStrategy::getIsEnabled, 1)
        );
        vo.setGlobalDiversityWeight(diversityStrategy != null && diversityStrategy.getStrategyValue() != null 
                ? Double.parseDouble(diversityStrategy.getStrategyValue()) : 0.3);
        
        RecommendStrategy coldStartStrategy = recommendStrategyMapper.selectOne(
                new LambdaQueryWrapper<RecommendStrategy>()
                        .eq(RecommendStrategy::getStrategyKey, "cold_start_hot_ratio")
                        .eq(RecommendStrategy::getIsEnabled, 1)
        );
        vo.setColdStartHotRatio(coldStartStrategy != null && coldStartStrategy.getStrategyValue() != null 
                ? Double.parseDouble(coldStartStrategy.getStrategyValue()) : 0.8);
        
        RecommendStrategy longTailStrategy = recommendStrategyMapper.selectOne(
                new LambdaQueryWrapper<RecommendStrategy>()
                        .eq(RecommendStrategy::getStrategyKey, "long_tail_ratio")
                        .eq(RecommendStrategy::getIsEnabled, 1)
        );
        vo.setLongTailRatio(longTailStrategy != null && longTailStrategy.getStrategyValue() != null 
                ? Double.parseDouble(longTailStrategy.getStrategyValue()) : 0.2);
        
        RecommendStrategy thresholdStrategy = recommendStrategyMapper.selectOne(
                new LambdaQueryWrapper<RecommendStrategy>()
                        .eq(RecommendStrategy::getStrategyKey, "long_tail_threshold")
                        .eq(RecommendStrategy::getIsEnabled, 1)
        );
        vo.setLongTailThreshold(thresholdStrategy != null && thresholdStrategy.getStrategyValue() != null 
                ? Integer.parseInt(thresholdStrategy.getStrategyValue()) : 10);
        
        RecommendStrategy enableLongTailStrategy = recommendStrategyMapper.selectOne(
                new LambdaQueryWrapper<RecommendStrategy>()
                        .eq(RecommendStrategy::getStrategyKey, "enable_long_tail")
                        .eq(RecommendStrategy::getIsEnabled, 1)
        );
        vo.setEnableLongTail(enableLongTailStrategy != null && "true".equals(enableLongTailStrategy.getStrategyValue()));
        
        return vo;
    }
    
    @Override
    @Transactional
    public void updateRecommendStrategy(RecommendStrategyVO strategy) {
        // 更新或创建推荐策略配置
        updateOrCreateStrategy("hot_recommend_ratio", String.valueOf(strategy.getHotRecommendRatio()), "热门推荐比例");
        updateOrCreateStrategy("global_diversity_weight", String.valueOf(strategy.getGlobalDiversityWeight()), "全局多样性权重");
        updateOrCreateStrategy("cold_start_hot_ratio", String.valueOf(strategy.getColdStartHotRatio()), "冷启动热门推荐比例");
        updateOrCreateStrategy("long_tail_ratio", String.valueOf(strategy.getLongTailRatio()), "长尾推荐比例");
        updateOrCreateStrategy("long_tail_threshold", String.valueOf(strategy.getLongTailThreshold()), "长尾图书阈值");
        updateOrCreateStrategy("enable_long_tail", String.valueOf(strategy.getEnableLongTail()), "是否启用长尾推荐");
    }

    // ========== 推荐运营管理 ==========

    @Override
    public java.util.List<RecommendBookVO> previewRecommendations(String recommendType, Integer limit, Double personalizationWeight, Double diversityWeight) {
        int l = (limit == null || limit <= 0) ? 6 : Math.min(limit, 50);
        String type = (recommendType == null || recommendType.trim().isEmpty()) ? "home" : recommendType.trim();

        switch (type) {
            case "home":
                return recommendService.getHomeRecommendations(null, personalizationWeight, diversityWeight)
                        .stream().limit(l).collect(java.util.stream.Collectors.toList());
            case "hot":
                return recommendService.getHotBooks(l).stream().map(b -> {
                    RecommendBookVO vo = new RecommendBookVO();
                    BeanUtils.copyProperties(b, vo);
                    vo.setBookId(b.getId());
                    vo.setReason("热门榜单预览");
                    return vo;
                }).collect(java.util.stream.Collectors.toList());
            case "new":
                return recommendService.getNewBooks(l).stream().map(b -> {
                    RecommendBookVO vo = new RecommendBookVO();
                    BeanUtils.copyProperties(b, vo);
                    vo.setBookId(b.getId());
                    vo.setReason("新书预览");
                    return vo;
                }).collect(java.util.stream.Collectors.toList());
            case "long-tail":
                return recommendService.getLongTailRecommendations(l);
            default:
                return recommendService.getHomeRecommendations(null, personalizationWeight, diversityWeight)
                        .stream().limit(l).collect(java.util.stream.Collectors.toList());
        }
    }

    @Override
    public PageResult<RecommendBlacklist> listRecommendBlacklist(Long page, Long size, String recommendType, String position, Long bookId) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecommendBlacklist> p = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        LambdaQueryWrapper<RecommendBlacklist> w = new LambdaQueryWrapper<>();
        if (bookId != null) w.eq(RecommendBlacklist::getBookId, bookId);
        if (recommendType != null && !recommendType.trim().isEmpty()) w.eq(RecommendBlacklist::getRecommendType, recommendType.trim());
        if (position != null && !position.trim().isEmpty()) w.eq(RecommendBlacklist::getPosition, position.trim());
        w.orderByDesc(RecommendBlacklist::getCreateTime);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecommendBlacklist> r = recommendBlacklistMapper.selectPage(p, w);
        return PageResult.of(page, size, r.getTotal(), r.getRecords());
    }

    @Override
    @Transactional
    public void upsertRecommendBlacklist(BlacklistDTO dto) {
        RecommendBlacklist exist = recommendBlacklistMapper.selectOne(
                new LambdaQueryWrapper<RecommendBlacklist>()
                        .eq(RecommendBlacklist::getBookId, dto.getBookId())
                        .eq(dto.getRecommendType() != null, RecommendBlacklist::getRecommendType, dto.getRecommendType())
                        .eq(dto.getPosition() != null, RecommendBlacklist::getPosition, dto.getPosition())
        );
        if (exist == null) {
            RecommendBlacklist b = new RecommendBlacklist();
            b.setBookId(dto.getBookId());
            b.setRecommendType(emptyToNull(dto.getRecommendType()));
            b.setPosition(emptyToNull(dto.getPosition()));
            b.setReason(dto.getReason());
            b.setIsEnabled(dto.getIsEnabled() == null ? 1 : dto.getIsEnabled());
            b.setCreateTime(LocalDateTime.now());
            recommendBlacklistMapper.insert(b);
        } else {
            exist.setReason(dto.getReason());
            exist.setIsEnabled(dto.getIsEnabled() == null ? exist.getIsEnabled() : dto.getIsEnabled());
            recommendBlacklistMapper.updateById(exist);
        }
    }

    @Override
    @Transactional
    public void deleteRecommendBlacklist(Long id) {
        recommendBlacklistMapper.deleteById(id);
    }

    @Override
    public PageResult<BookColdStart> listColdStart(Long page, Long size, Long bookId, Integer isEnabled) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BookColdStart> p = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        LambdaQueryWrapper<BookColdStart> w = new LambdaQueryWrapper<>();
        if (bookId != null) w.eq(BookColdStart::getBookId, bookId);
        if (isEnabled != null) w.eq(BookColdStart::getIsEnabled, isEnabled);
        w.orderByDesc(BookColdStart::getUpdateTime);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BookColdStart> r = bookColdStartMapper.selectPage(p, w);
        return PageResult.of(page, size, r.getTotal(), r.getRecords());
    }

    @Override
    @Transactional
    public void upsertColdStart(ColdStartDTO dto) {
        BookColdStart exist = bookColdStartMapper.selectOne(
                new LambdaQueryWrapper<BookColdStart>().eq(BookColdStart::getBookId, dto.getBookId())
        );
        LocalDateTime st = parseDateTime(dto.getStartTime());
        LocalDateTime et = parseDateTime(dto.getEndTime());
        if (exist == null) {
            BookColdStart cs = new BookColdStart();
            cs.setBookId(dto.getBookId());
            cs.setInitialExposure(dto.getInitialExposure() == null ? 0 : dto.getInitialExposure());
            cs.setManualWeight(dto.getManualWeight() == null ? 0.0 : dto.getManualWeight());
            cs.setStartTime(st);
            cs.setEndTime(et);
            cs.setIsEnabled(dto.getIsEnabled() == null ? 1 : dto.getIsEnabled());
            cs.setCreateTime(LocalDateTime.now());
            cs.setUpdateTime(LocalDateTime.now());
            bookColdStartMapper.insert(cs);
        } else {
            if (dto.getInitialExposure() != null) exist.setInitialExposure(dto.getInitialExposure());
            if (dto.getManualWeight() != null) exist.setManualWeight(dto.getManualWeight());
            exist.setStartTime(st);
            exist.setEndTime(et);
            if (dto.getIsEnabled() != null) exist.setIsEnabled(dto.getIsEnabled());
            exist.setUpdateTime(LocalDateTime.now());
            bookColdStartMapper.updateById(exist);
        }
    }

    @Override
    @Transactional
    public void deleteColdStart(Long id) {
        bookColdStartMapper.deleteById(id);
    }

    // ========== 数据统计分析 ==========

    @Override
    public java.util.List<StatPointVO> userGrowth(Integer days) {
        int d = (days == null || days <= 0) ? 30 : Math.min(days, 365);
        java.util.List<java.util.Map<String, Object>> rows = adminStatsMapper.userGrowth(d);
        java.util.List<StatPointVO> list = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> r : rows) {
            StatPointVO vo = new StatPointVO();
            vo.setDate(String.valueOf(r.get("d")));
            vo.setCount(((Number) r.get("c")).longValue());
            list.add(vo);
        }
        return list;
    }

    @Override
    public java.util.List<BookRankVO> bookRank(String metric, Integer days, Integer limit) {
        int d = (days == null || days <= 0) ? 30 : Math.min(days, 365);
        int l = (limit == null || limit <= 0) ? 20 : Math.min(limit, 100);
        String m = (metric == null) ? "click" : metric.trim();
        java.util.List<java.util.Map<String, Object>> rows;
        if ("favorite".equalsIgnoreCase(m)) {
            rows = adminStatsMapper.bookRankByFavorite(d, l);
        } else {
            rows = adminStatsMapper.bookRankByRecommendClick(d, l);
        }
        java.util.List<BookRankVO> list = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> r : rows) {
            BookRankVO vo = new BookRankVO();
            vo.setBookId(((Number) r.get("bookId")).longValue());
            vo.setTitle((String) r.get("title"));
            vo.setAuthor((String) r.get("author"));
            vo.setCoverUrl((String) r.get("coverUrl"));
            vo.setCount(((Number) r.get("c")).longValue());
            list.add(vo);
        }
        return list;
    }

    @Override
    public RecommendFunnelVO recommendFunnel(String recommendType, Integer days) {
        int d = (days == null || days <= 0) ? 30 : Math.min(days, 365);
        String t = (recommendType == null || recommendType.trim().isEmpty()) ? "home" : recommendType.trim();
        java.util.Map<String, Object> r = adminStatsMapper.recommendFunnel(t, d);
        long exposure = ((Number) r.getOrDefault("exposureCount", 0)).longValue();
        long click = ((Number) r.getOrDefault("clickCount", 0)).longValue();
        long fav = ((Number) r.getOrDefault("favoriteCount", 0)).longValue();
        long rating = ((Number) r.getOrDefault("ratingCount", 0)).longValue();
        RecommendFunnelVO vo = new RecommendFunnelVO();
        vo.setRecommendType(t);
        vo.setExposureCount(exposure);
        vo.setClickCount(click);
        vo.setFavoriteCount(fav);
        vo.setRatingCount(rating);
        vo.setCtr(exposure > 0 ? (double) click / exposure * 100 : 0.0);
        vo.setClickToFavorite(click > 0 ? (double) fav / click * 100 : 0.0);
        vo.setFavoriteToRating(fav > 0 ? (double) rating / fav * 100 : 0.0);
        return vo;
    }

    private String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            String normalized = s.trim().replace('T', ' ');
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (normalized.length() == 10) {
                // yyyy-MM-dd
                java.time.LocalDate d = java.time.LocalDate.parse(normalized, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
                return d.atStartOfDay();
            }
            if (normalized.length() == 16) {
                // yyyy-MM-dd HH:mm
                normalized = normalized + ":00";
            }
            return LocalDateTime.parse(normalized, fmt);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 更新或创建推荐策略配置
     */
    private void updateOrCreateStrategy(String key, String value, String description) {
        RecommendStrategy strategy = recommendStrategyMapper.selectOne(
                new LambdaQueryWrapper<RecommendStrategy>().eq(RecommendStrategy::getStrategyKey, key)
        );
        
        if (strategy == null) {
            strategy = new RecommendStrategy();
            strategy.setStrategyKey(key);
            strategy.setCreateTime(LocalDateTime.now());
        }
        
        strategy.setStrategyValue(value);
        strategy.setDescription(description);
        strategy.setIsEnabled(1);
        strategy.setUpdateTime(LocalDateTime.now());
        
        if (strategy.getId() == null) {
            recommendStrategyMapper.insert(strategy);
        } else {
            recommendStrategyMapper.updateById(strategy);
        }
    }
    
    // ========== 借阅管理 ==========
    
    @Override
    public PageResult<BorrowRecordVO> listBorrowRecords(Long page, Long size, Integer status, Integer auditStatus, String keyword) {
        Page<BorrowRecord> bookPage = new Page<>(page, size);
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(BorrowRecord::getStatus, status);
        }
        if (auditStatus != null) {
            wrapper.eq(BorrowRecord::getAuditStatus, auditStatus);
        }
        wrapper.orderByDesc(BorrowRecord::getApplyTime);
        
        Page<BorrowRecord> result = borrowRecordMapper.selectPage(bookPage, wrapper);
        
        List<BorrowRecordVO> voList = result.getRecords().stream().map(record -> {
            BorrowRecordVO vo = new BorrowRecordVO();
            BeanUtils.copyProperties(record, vo);
            
            // 查询用户信息
            User user = userMapper.selectById(record.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
            }
            
            // 查询图书信息
            Book book = bookMapper.selectById(record.getBookId());
            if (book != null) {
                vo.setBookTitle(book.getTitle());
                vo.setBookIsbn(book.getIsbn());
                vo.setBookCoverUrl(book.getCoverUrl());
            }
            
            // 查询审核管理员信息
            if (record.getAuditAdminId() != null) {
                User admin = userMapper.selectById(record.getAuditAdminId());
                if (admin != null) {
                    vo.setAuditAdminName(admin.getNickname());
                }
            }
            
            // 计算逾期天数
            if (record.getStatus() == 1 && record.getExpectedReturnTime() != null) {
                LocalDateTime now = LocalDateTime.now();
                if (now.isAfter(record.getExpectedReturnTime())) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(record.getExpectedReturnTime(), now);
                    vo.setOverdueDays((int) days);
                } else {
                    vo.setOverdueDays(0);
                }
            }
            
            // 关键字过滤（用户名、昵称、书名、ISBN）
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.trim().toLowerCase();
                boolean match = (vo.getUsername() != null && vo.getUsername().toLowerCase().contains(kw))
                        || (vo.getNickname() != null && vo.getNickname().toLowerCase().contains(kw))
                        || (vo.getBookTitle() != null && vo.getBookTitle().toLowerCase().contains(kw))
                        || (vo.getBookIsbn() != null && vo.getBookIsbn().contains(kw));
                if (!match) {
                    return null;
                }
            }
            
            return vo;
        }).filter(vo -> vo != null).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), voList);
    }
    
    @Override
    @Transactional
    public void auditBorrowRecord(Long borrowId, Integer auditStatus) {
        BorrowRecord record = borrowRecordMapper.selectById(borrowId);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        
        if (auditStatus == 1) {
            // 审核通过，设置借阅时间；预计归还时间优先使用用户在申请时填写的值，否则默认30天
            record.setBorrowTime(LocalDateTime.now());
            if (record.getExpectedReturnTime() == null) {
                record.setExpectedReturnTime(LocalDateTime.now().plusDays(30));
            }
            record.setStatus(1); // 借阅中

            // 更新图书借阅次数（在真正借出时才计数）
            Book book = bookMapper.selectById(record.getBookId());
            if (book != null) {
                book.setBorrowCount(book.getBorrowCount() + 1);
                bookMapper.updateById(book);
            }
        } else if (auditStatus == 2) {
            // 审核拒绝：保持未借出状态
            record.setStatus(0);
        }
        
        record.setAuditStatus(auditStatus);
        record.setAuditTime(LocalDateTime.now());
        record.setAuditAdminId(UserContext.getCurrentUserId());
        
        borrowRecordMapper.updateById(record);
    }
    
    @Override
    @Transactional
    public void confirmReturn(Long borrowId) {
        BorrowRecord record = borrowRecordMapper.selectById(borrowId);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        
        if (record.getStatus() == 0) {
            throw new BusinessException("此图书已归还");
        }

        if (record.getReturnApplyTime() == null) {
            throw new BusinessException("用户尚未提交还书申请");
        }
        
        record.setStatus(0); // 已归还
        record.setReturnTime(LocalDateTime.now());
        
        // 计算逾期天数
        if (record.getExpectedReturnTime() != null && LocalDateTime.now().isAfter(record.getExpectedReturnTime())) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(record.getExpectedReturnTime(), LocalDateTime.now());
            record.setOverdueDays((int) days);
        } else {
            record.setOverdueDays(0);
        }
        
        borrowRecordMapper.updateById(record);
    }
    
    @Override
    public PageResult<BorrowRecordVO> getOverdueRecords(Long page, Long size) {
        Page<BorrowRecord> bookPage = new Page<>(page, size);
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getStatus, 1); // 借阅中
        wrapper.isNotNull(BorrowRecord::getExpectedReturnTime);
        wrapper.lt(BorrowRecord::getExpectedReturnTime, LocalDateTime.now()); // 预计归还时间已过
        wrapper.orderByAsc(BorrowRecord::getExpectedReturnTime);
        
        Page<BorrowRecord> result = borrowRecordMapper.selectPage(bookPage, wrapper);
        
        List<BorrowRecordVO> voList = result.getRecords().stream().map(record -> {
            BorrowRecordVO vo = new BorrowRecordVO();
            BeanUtils.copyProperties(record, vo);
            
            // 查询用户信息
            User user = userMapper.selectById(record.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
            }
            
            // 查询图书信息
            Book book = bookMapper.selectById(record.getBookId());
            if (book != null) {
                vo.setBookTitle(book.getTitle());
                vo.setBookIsbn(book.getIsbn());
                vo.setBookCoverUrl(book.getCoverUrl());
            }
            
            // 计算逾期天数
            long days = java.time.temporal.ChronoUnit.DAYS.between(record.getExpectedReturnTime(), LocalDateTime.now());
            vo.setOverdueDays((int) days);
            
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), voList);
    }
    
    // ========== 行为日志管理 ==========
    
    @Override
    public PageResult<UserBehaviorVO> listUserBehaviors(Long page, Long size, Long userId, String behaviorType, String keyword) {
        Page<UserBehavior> bookPage = new Page<>(page, size);
        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(UserBehavior::getUserId, userId);
        }
        if (behaviorType != null && !behaviorType.trim().isEmpty()) {
            wrapper.eq(UserBehavior::getBehaviorType, behaviorType.trim());
        }
        wrapper.orderByDesc(UserBehavior::getCreateTime);
        
        Page<UserBehavior> result = userBehaviorMapper.selectPage(bookPage, wrapper);
        
        List<UserBehaviorVO> voList = result.getRecords().stream().map(behavior -> {
            UserBehaviorVO vo = new UserBehaviorVO();
            BeanUtils.copyProperties(behavior, vo);
            
            // 查询用户信息
            User user = userMapper.selectById(behavior.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
            }
            
            // 查询图书信息
            Book book = bookMapper.selectById(behavior.getBookId());
            if (book != null) {
                vo.setBookTitle(book.getTitle());
                vo.setBookIsbn(book.getIsbn());
                vo.setBookCoverUrl(book.getCoverUrl());
            }
            
            // 关键字过滤（用户名、昵称、书名、ISBN）
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.trim().toLowerCase();
                boolean match = (vo.getUsername() != null && vo.getUsername().toLowerCase().contains(kw))
                        || (vo.getNickname() != null && vo.getNickname().toLowerCase().contains(kw))
                        || (vo.getBookTitle() != null && vo.getBookTitle().toLowerCase().contains(kw))
                        || (vo.getBookIsbn() != null && vo.getBookIsbn().contains(kw));
                if (!match) {
                    return null;
                }
            }
            
            return vo;
        }).filter(vo -> vo != null).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), voList);
    }
}

