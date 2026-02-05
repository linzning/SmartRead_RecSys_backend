package org.example.backend.modules.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.backend.entity.*;
import org.example.backend.entity.Topic;
import org.example.backend.modules.book.repository.BookMapper;
import org.example.backend.modules.book.repository.BookTopicMapper;
import org.example.backend.modules.book.repository.TopicMapper;
import org.example.backend.modules.interaction.repository.BorrowRecordMapper;
import org.example.backend.modules.recommend.repository.*;
import org.example.backend.modules.recommend.service.RecommendService;
import org.example.backend.modules.user.repository.UserInterestGuideMapper;
import org.example.backend.vo.admin.RecommendStrategyVO;
import org.example.backend.vo.book.BookVO;
import org.example.backend.vo.recommend.RecommendBookVO;
import org.example.backend.vo.recommend.TopicVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现
 */
@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookTopicMapper bookTopicMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private RecommendExposureMapper recommendExposureMapper;

    @Autowired
    private RecommendClickMapper recommendClickMapper;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private UserPreferenceMapper userPreferenceMapper;

    @Autowired
    private UserInterestGuideMapper userInterestGuideMapper;

    @Autowired
    private RecommendStrategyMapper recommendStrategyMapper;

    @Autowired
    private RecommendBlacklistMapper recommendBlacklistMapper;

    @Autowired
    private BookColdStartMapper bookColdStartMapper;


    /**
     * // TODO 推荐 首页推荐总体流程：
     * <p>
     * 1. 加载系统推荐策略配置（权重、比例、是否开启长尾等）
     * 2. 初始化推荐结果列表 + 加载首页场景黑名单（用于去重和过滤）
     * <p>
     * 3. 注入运营配置的冷启动推荐书籍（强运营位，所有用户优先展示）
     * <p>
     * 4. 判断是否为新用户（冷启动用户）：
     * - 是新用户：按策略比例推荐热门图书（基于借阅次数）
     * - 是老用户：基于用户借阅历史和偏好生成个性化推荐
     * <p>
     * 5. 若推荐数量不足：
     * - 按多样性权重补充不同主题/分类的图书（防止信息茧房）
     * <p>
     * 6. 若仍不足且开启长尾策略：
     * - 补充低热度长尾图书（挖掘冷门优质内容）
     * <p>
     * 7. 若推荐数量仍不足：
     * - 使用热门图书兜底补齐（排除黑名单与已推荐图书）
     * <p>
     * 8. 最终返回不超过 6 条首页推荐结果
     */
    @Override
    public List<RecommendBookVO> getHomeRecommendations(Long userId, Double personalizationWeight, Double diversityWeight) {
        // 获取推荐策略配置
        RecommendStrategyVO strategy = getRecommendStrategy();

        // 如果没有传入权重，使用全局配置
        if (personalizationWeight == null || diversityWeight == null) {
            personalizationWeight = 1.0 - strategy.getGlobalDiversityWeight();
            diversityWeight = strategy.getGlobalDiversityWeight();
        }

        List<RecommendBookVO> recommendations = new ArrayList<>();
        // 全局 + home 场景的黑名单
        Set<Long> excludeBookIds = new HashSet<>(loadBlacklistedBookIds("home", null));

        // 先注入冷启动运营配置的图书（不受是否新用户限制）
        List<BookColdStart> coldStarts = loadActiveColdStartConfigs();
        for (BookColdStart cs : coldStarts) {
            if (recommendations.size() >= 6) break;
            Long bookId = cs.getBookId();
            if (bookId == null || excludeBookIds.contains(bookId)) {
                continue;
            }
            Book book = bookMapper.selectById(bookId);
            if (book == null || book.getStatus() == null || book.getStatus() != 1) {
                continue;
            }
            RecommendBookVO vo = createRecommendVO(book);
            vo.setReason("冷启动运营推荐");
            recommendations.add(vo);
            excludeBookIds.add(bookId);
        }

        // 判断是否为新用户（冷启动）
        boolean isColdStart = isColdStartUser(userId);

        if (isColdStart) {
            // 冷启动：推荐热门图书
            double hotRatio = strategy.getColdStartHotRatio();
            int hotCount = (int) (6 * hotRatio);
            List<Book> hotBooks = bookMapper.selectList(
                    new LambdaQueryWrapper<Book>()
                            .eq(Book::getStatus, 1)
                            .orderByDesc(Book::getBorrowCount)
                            .last("LIMIT " + hotCount)
            );

            for (Book book : hotBooks) {
                RecommendBookVO vo = createRecommendVO(book);
                vo.setReason("热门推荐（新用户）");
                recommendations.add(vo);
                excludeBookIds.add(book.getId());
            }
        } else if (userId != null) {
            // 个性化推荐：基于用户借阅历史和偏好
            List<RecommendBookVO> personalized = getPersonalizedRecommendations(userId, personalizationWeight, excludeBookIds);
            recommendations.addAll(personalized);
            personalized.forEach(r -> excludeBookIds.add(r.getBookId()));
        }

        // 多样性推荐：补充不同主题的图书
        if (recommendations.size() < 6 && diversityWeight > 0) {
            int diversityCount = (int) (6 * diversityWeight);
            List<RecommendBookVO> diverse = getDiverseRecommendations(userId, diversityCount, excludeBookIds);
            recommendations.addAll(diverse);
            diverse.forEach(r -> excludeBookIds.add(r.getBookId()));
        }

        // 长尾推荐：如果启用，添加长尾图书
        if (strategy.getEnableLongTail() != null && strategy.getEnableLongTail() && recommendations.size() < 6) {
            int longTailCount = (int) (6 * strategy.getLongTailRatio());
            List<RecommendBookVO> longTail = getLongTailRecommendations(longTailCount, excludeBookIds, strategy.getLongTailThreshold());
            recommendations.addAll(longTail);
            longTail.forEach(r -> excludeBookIds.add(r.getBookId()));
        }

        // 如果推荐不足，补充热门图书
        if (recommendations.size() < 6) {
            int remaining = 6 - recommendations.size();
            List<Book> hotBooks = bookMapper.selectList(
                    new LambdaQueryWrapper<Book>()
                            .eq(Book::getStatus, 1)
                            .notIn(!excludeBookIds.isEmpty(), Book::getId, excludeBookIds)
                            .orderByDesc(Book::getBorrowCount)
                            .last("LIMIT " + remaining)
            );

            for (Book book : hotBooks) {
                RecommendBookVO vo = createRecommendVO(book);
                vo.setReason("热门推荐");
                recommendations.add(vo);
            }
        }

        return recommendations.stream().limit(6).collect(Collectors.toList());
    }

    /**
     * 判断是否为新用户（冷启动）
     */
    private boolean isColdStartUser(Long userId) {
        if (userId == null) {
            return true;
        }

        // 检查是否完成兴趣引导
        UserInterestGuide guide = userInterestGuideMapper.selectOne(
                new LambdaQueryWrapper<UserInterestGuide>().eq(UserInterestGuide::getUserId, userId)
        );
        if (guide == null || guide.getIsCompleted() == null || guide.getIsCompleted() == 0) {
            return true;
        }

        // 检查借阅记录数量（少于3本视为新用户）
        Long borrowCount = borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getUserId, userId)
        );
        return borrowCount < 3;
    }

    /**
     *   获取个性化推荐
     */
    private List<RecommendBookVO> getPersonalizedRecommendations(Long userId, Double weight, Set<Long> excludeBookIds) {
        List<RecommendBookVO> recommendations = new ArrayList<>();

        // 获取用户偏好
        List<UserPreference> preferences = userPreferenceMapper.selectList(
                new LambdaQueryWrapper<UserPreference>().eq(UserPreference::getUserId, userId)
        );

        // 获取用户借阅历史
            List<BorrowRecord> borrowRecords = borrowRecordMapper.selectList(
                    new LambdaQueryWrapper<BorrowRecord>()
                            .eq(BorrowRecord::getUserId, userId)
                            .orderByDesc(BorrowRecord::getBorrowTime)
                            .last("LIMIT 10")
            );

                Set<Long> borrowedBookIds = borrowRecords.stream()
                        .map(BorrowRecord::getBookId)
                        .collect(Collectors.toSet());

        // 基于偏好主题推荐
        if (!preferences.isEmpty()) {
            List<String> preferredTopics = preferences.stream()
                    .filter(p -> "topic".equals(p.getPreferenceType()))
                    .map(UserPreference::getPreferenceValue)
                    .collect(Collectors.toList());

            if (!preferredTopics.isEmpty()) {
                List<BookTopic> bookTopics = bookTopicMapper.selectList(
                        new LambdaQueryWrapper<BookTopic>()
                                .in(BookTopic::getTopicName, preferredTopics)
                );

                Map<Long, Long> bookScore = new HashMap<>();
                for (BookTopic bt : bookTopics) {
                    if (!borrowedBookIds.contains(bt.getBookId()) && !excludeBookIds.contains(bt.getBookId())) {
                        bookScore.put(bt.getBookId(), bookScore.getOrDefault(bt.getBookId(), 0L) + 1);
                    }
                }

                List<Long> topBookIds = bookScore.entrySet().stream()
                        .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                        .limit((int) (6 * weight))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                if (!topBookIds.isEmpty()) {
                    List<Book> books = bookMapper.selectBatchIds(topBookIds);
                    for (Book book : books) {
                        RecommendBookVO vo = createRecommendVO(book);
                        vo.setReason("基于您的兴趣偏好：" + preferredTopics.get(0));
                        recommendations.add(vo);
                    }
                }
            }
        }

        // 基于借阅历史推荐相似图书
        if (!borrowRecords.isEmpty() && recommendations.size() < 6 * weight) {
            List<BookTopic> topics = bookTopicMapper.selectList(
                    new LambdaQueryWrapper<BookTopic>()
                            .in(BookTopic::getBookId, borrowedBookIds)
                );

                Map<String, Long> topicCount = topics.stream()
                        .collect(Collectors.groupingBy(BookTopic::getTopicName, Collectors.counting()));

                String topTopic = topicCount.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                    .orElse(null);

            if (topTopic != null) {
                List<BookTopic> similarTopics = bookTopicMapper.selectList(
                        new LambdaQueryWrapper<BookTopic>()
                                .eq(BookTopic::getTopicName, topTopic)
                );

                List<Long> similarBookIds = similarTopics.stream()
                        .map(BookTopic::getBookId)
                        .filter(id -> !borrowedBookIds.contains(id) && !excludeBookIds.contains(id))
                        .limit((int) (6 * weight))
                        .collect(Collectors.toList());

                if (!similarBookIds.isEmpty()) {
                    List<Book> books = bookMapper.selectBatchIds(similarBookIds);
                    for (Book book : books) {
                        if (recommendations.stream().noneMatch(r -> r.getBookId().equals(book.getId()))) {
                            RecommendBookVO vo = createRecommendVO(book);
                            vo.setReason("基于您最近阅读的主题：" + topTopic);
                    recommendations.add(vo);
                }
            }
                }
            }
        }

        return recommendations;
        }

    /**
     * 获取多样性推荐（不同主题的图书）
     */
    private List<RecommendBookVO> getDiverseRecommendations(Long userId, int count, Set<Long> excludeBookIds) {
        List<RecommendBookVO> recommendations = new ArrayList<>();

        // 获取用户已借阅的图书主题
        Set<String> userTopics = new HashSet<>();
        if (userId != null) {
            List<BorrowRecord> borrowRecords = borrowRecordMapper.selectList(
                    new LambdaQueryWrapper<BorrowRecord>()
                            .eq(BorrowRecord::getUserId, userId)
                            .last("LIMIT 20")
            );

            if (!borrowRecords.isEmpty()) {
                List<Long> bookIds = borrowRecords.stream()
                        .map(BorrowRecord::getBookId)
                        .collect(Collectors.toList());

                List<BookTopic> topics = bookTopicMapper.selectList(
                        new LambdaQueryWrapper<BookTopic>().in(BookTopic::getBookId, bookIds)
                );
                userTopics = topics.stream()
                        .map(BookTopic::getTopicName)
                        .collect(Collectors.toSet());
            }
        }

        // 获取不同主题的图书
        List<Topic> allTopics = topicMapper.selectList(
                new LambdaQueryWrapper<Topic>().eq(Topic::getStatus, 1)
        );

        final List<String> finalUserTopics = new ArrayList<>(userTopics);
        final int finalCount = count;

        List<String> diverseTopics = allTopics.stream()
                .map(Topic::getName)
                .filter(t -> !finalUserTopics.contains(t))
                .limit(finalCount)
                .collect(Collectors.toList());


        for (String topic : diverseTopics) {
            List<BookTopic> bookTopics = bookTopicMapper.selectList(
                    new LambdaQueryWrapper<BookTopic>()
                            .eq(BookTopic::getTopicName, topic)
                            .last("LIMIT 1")
            );

            if (!bookTopics.isEmpty()) {
                Long bookId = bookTopics.get(0).getBookId();
                if (!excludeBookIds.contains(bookId)) {
                    Book book = bookMapper.selectById(bookId);
                    if (book != null && book.getStatus() == 1) {
                        RecommendBookVO vo = createRecommendVO(book);
                        vo.setReason("探索新主题：" + topic);
                    recommendations.add(vo);
                        excludeBookIds.add(bookId);
                    }
                }
            }
        }

        return recommendations;
    }

    /**
     * 获取长尾推荐（借阅次数少的优质图书）
     */
    private List<RecommendBookVO> getLongTailRecommendations(int count, Set<Long> excludeBookIds, Integer threshold) {
        List<RecommendBookVO> recommendations = new ArrayList<>();

        // 追加当前场景的黑名单（长尾推荐）
        excludeBookIds.addAll(loadBlacklistedBookIds("long-tail", null));

        // 查询借阅次数小于阈值但评分较高的图书
        List<Book> longTailBooks = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .le(Book::getBorrowCount, threshold != null ? threshold : 10)
                        .gt(Book::getAvgRating, 3.5)
                        .notIn(!excludeBookIds.isEmpty(), Book::getId, excludeBookIds)
                        .orderByDesc(Book::getAvgRating)
                        .orderByAsc(Book::getBorrowCount)
                        .last("LIMIT " + count)
        );

        for (Book book : longTailBooks) {
            RecommendBookVO vo = createRecommendVO(book);
            vo.setReason("冷门佳作（评分 " + book.getAvgRating() + "）");
            recommendations.add(vo);
        }

        return recommendations;
    }

    /**
     * 获取推荐策略配置（如果表不存在则返回默认值）
     */
    private RecommendStrategyVO getRecommendStrategy() {
        RecommendStrategyVO vo = new RecommendStrategyVO();

        try {
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
        } catch (Exception e) {
            // 如果表不存在或查询失败，使用默认值
            System.err.println("获取推荐策略配置失败，使用默认值: " + e.getMessage());
            vo.setHotRecommendRatio(0.3);
            vo.setGlobalDiversityWeight(0.3);
            vo.setColdStartHotRatio(0.8);
            vo.setLongTailRatio(0.2);
            vo.setLongTailThreshold(10);
            vo.setEnableLongTail(false);
        }

        return vo;
    }

    /**
     * 创建推荐VO
     */
    private RecommendBookVO createRecommendVO(Book book) {
        RecommendBookVO vo = new RecommendBookVO();
        BeanUtils.copyProperties(book, vo);
        vo.setBookId(book.getId());
        // 手动转换 avgRating（BigDecimal -> Double）
        if (book.getAvgRating() != null) {
            vo.setAvgRating(book.getAvgRating().doubleValue());
        }
        return vo;
    }

    @Override
    public List<BookVO> getNewBooks(int limit) {
        // 黑名单：新书推荐场景
        Set<Long> blacklistIds = loadBlacklistedBookIds("new", null);
        List<Book> books = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .notIn(!blacklistIds.isEmpty(), Book::getId, blacklistIds)
                        .orderByDesc(Book::getCreateTime)
                        .last("LIMIT " + limit)
        );

        return convertToBookVOList(books);
    }

    @Override
    public List<BookVO> getHotBooks(int limit) {
        // 黑名单：热门推荐场景
        Set<Long> blacklistIds = loadBlacklistedBookIds("hot", null);
        List<Book> books = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .notIn(!blacklistIds.isEmpty(), Book::getId, blacklistIds)
                        .orderByDesc(Book::getBorrowCount)
                        .last("LIMIT " + limit)
        );

        return convertToBookVOList(books);
    }

    @Override
    public List<BookVO> getBooksByTopic(String topic, int limit) {
        // 查询该主题的图书ID
        List<BookTopic> bookTopics = bookTopicMapper.selectList(
                new LambdaQueryWrapper<BookTopic>()
                        .eq(BookTopic::getTopicName, topic)
                        .last("LIMIT " + limit)
        );

        if (bookTopics.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> bookIds = bookTopics.stream()
                .map(BookTopic::getBookId)
                .collect(Collectors.toList());

        List<Book> books = bookMapper.selectBatchIds(bookIds);

        return convertToBookVOList(books);
    }

    @Override
    public List<RecommendBookVO> getSimilarBooks(Long bookId, int limit) {
        // 获取图书主题
        List<BookTopic> topics = bookTopicMapper.selectList(
                new LambdaQueryWrapper<BookTopic>()
                        .eq(BookTopic::getBookId, bookId)
        );

        if (topics.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> topicNames = topics.stream()
                .map(BookTopic::getTopicName)
                .collect(Collectors.toList());

        // 查询相同主题的其他图书
        List<BookTopic> similarTopics = bookTopicMapper.selectList(
                new LambdaQueryWrapper<BookTopic>()
                        .in(BookTopic::getTopicName, topicNames)
                        .ne(BookTopic::getBookId, bookId)
        );

        Map<Long, Long> bookScore = new HashMap<>();
        for (BookTopic topic : similarTopics) {
            bookScore.put(topic.getBookId(), bookScore.getOrDefault(topic.getBookId(), 0L) + 1);
        }

        // 按相似度排序
        List<Long> similarBookIds = bookScore.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());

        if (similarBookIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 黑名单：相似推荐场景
        Set<Long> blacklistIds = loadBlacklistedBookIds("similar", null);
        List<Long> filteredIds = similarBookIds.stream()
                .filter(id -> !blacklistIds.contains(id))
                .limit(limit)
                .collect(Collectors.toList());

        List<Book> books = filteredIds.isEmpty() ? Collections.emptyList() : bookMapper.selectBatchIds(filteredIds);

        return books.stream().map(book -> {
            RecommendBookVO vo = new RecommendBookVO();
            BeanUtils.copyProperties(book, vo);
            vo.setBookId(book.getId());
            vo.setReason("内容相似度 " + bookScore.get(book.getId()) * 10 + "%");
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RecommendBookVO> getUserAlsoRead(Long bookId, int limit) {
        // 获取借阅过该书的用户
        List<BorrowRecord> records = borrowRecordMapper.selectList(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getBookId, bookId)
        );

        if (records.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> userIds = records.stream()
                .map(BorrowRecord::getUserId)
                .collect(Collectors.toSet());

        // 查询这些用户还借阅了哪些书
        List<BorrowRecord> otherRecords = borrowRecordMapper.selectList(
                new LambdaQueryWrapper<BorrowRecord>()
                        .in(BorrowRecord::getUserId, userIds)
                        .ne(BorrowRecord::getBookId, bookId)
        );

        Map<Long, Long> bookCount = otherRecords.stream()
                .collect(Collectors.groupingBy(BorrowRecord::getBookId, Collectors.counting()));

        List<Long> bookIds = bookCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());

        if (bookIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 黑名单：关联推荐场景
        Set<Long> blacklistIds = loadBlacklistedBookIds("also-read", null);
        List<Long> filteredIds = bookIds.stream()
                .filter(id -> !blacklistIds.contains(id))
                .limit(limit)
                .collect(Collectors.toList());

        List<Book> books = filteredIds.isEmpty() ? Collections.emptyList() : bookMapper.selectBatchIds(filteredIds);

        return books.stream().map(book -> {
            RecommendBookVO vo = new RecommendBookVO();
            BeanUtils.copyProperties(book, vo);
            vo.setBookId(book.getId());
            vo.setReason("借阅重合度 " + bookCount.get(book.getId()) * 10 + "%");
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recordExposure(Long userId, Long bookId, String recommendType, String position) {
        RecommendExposure exposure = new RecommendExposure();
        exposure.setUserId(userId);
        exposure.setBookId(bookId);
        exposure.setRecommendType(recommendType);
        exposure.setPosition(position);
        exposure.setExposureTime(LocalDateTime.now());
        recommendExposureMapper.insert(exposure);
    }

    @Override
    @Transactional
    public void recordClick(Long userId, Long bookId, String recommendType, String position) {
        RecommendClick click = new RecommendClick();
        click.setUserId(userId);
        click.setBookId(bookId);
        click.setRecommendType(recommendType);
        click.setPosition(position);
        click.setClickTime(LocalDateTime.now());
        recommendClickMapper.insert(click);
    }

    @Override
    @Transactional
    public void feedback(Long userId, Long bookId, String feedbackType, String reason) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setBookId(bookId);
        feedback.setFeedbackType(feedbackType);
        feedback.setReason(reason);
        feedback.setCreateTime(LocalDateTime.now());
        feedbackMapper.insert(feedback);
    }

    /**
     * 加载当前时间有效且启用的冷启动配置
     */
    private List<BookColdStart> loadActiveColdStartConfigs() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<BookColdStart> wrapper = new LambdaQueryWrapper<BookColdStart>()
                .eq(BookColdStart::getIsEnabled, 1)
                .and(w -> w.isNull(BookColdStart::getStartTime).or().le(BookColdStart::getStartTime, now))
                .and(w -> w.isNull(BookColdStart::getEndTime).or().ge(BookColdStart::getEndTime, now));

        List<BookColdStart> list = bookColdStartMapper.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        // 手动权重优先，其次初始曝光，再次更新时间
        return list.stream()
                .sorted(Comparator
                        .comparing((BookColdStart cs) -> cs.getManualWeight() == null ? 0.0 : cs.getManualWeight()).reversed()
                        .thenComparing(cs -> cs.getInitialExposure() == null ? 0 : cs.getInitialExposure(), Comparator.reverseOrder())
                        .thenComparing(cs -> cs.getUpdateTime() == null ? cs.getCreateTime() : cs.getUpdateTime(), Comparator.reverseOrder())
                )
                .collect(Collectors.toList());
    }

    /**
     * 加载启用中的黑名单图书ID
     *
     * @param recommendType 推荐类型（home/hot/new/long-tail/...），null 表示只匹配全局
     * @param position      推荐位，可为空
     */
    private Set<Long> loadBlacklistedBookIds(String recommendType, String position) {
        LambdaQueryWrapper<RecommendBlacklist> wrapper = new LambdaQueryWrapper<RecommendBlacklist>()
                .eq(RecommendBlacklist::getIsEnabled, 1);

        if (recommendType != null && !recommendType.trim().isEmpty()) {
            String type = recommendType.trim();
            wrapper.and(w -> w.isNull(RecommendBlacklist::getRecommendType)
                    .or().eq(RecommendBlacklist::getRecommendType, type));
        } else {
            wrapper.isNull(RecommendBlacklist::getRecommendType);
        }

        if (position != null && !position.trim().isEmpty()) {
            String pos = position.trim();
            wrapper.and(w -> w.isNull(RecommendBlacklist::getPosition)
                    .or().eq(RecommendBlacklist::getPosition, pos));
        }

        List<RecommendBlacklist> list = recommendBlacklistMapper.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return new HashSet<>();
        }
        return list.stream()
                .map(RecommendBlacklist::getBookId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 转换为BookVO列表
     */
    private List<BookVO> convertToBookVOList(List<Book> books) {
        return books.stream().map(book -> {
            BookVO vo = new BookVO();
            BeanUtils.copyProperties(book, vo);
            List<String> topics = bookTopicMapper.selectList(
                    new LambdaQueryWrapper<BookTopic>()
                            .eq(BookTopic::getBookId, book.getId())
            ).stream().map(BookTopic::getTopicName).collect(Collectors.toList());
            vo.setTopics(topics);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TopicVO> getPopularTopics(int limit) {
        // 查询所有上架图书的ID
        List<Book> activeBooks = bookMapper.selectList(
                new LambdaQueryWrapper<Book>().eq(Book::getStatus, 1)
        );

        if (activeBooks.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> bookIds = activeBooks.stream()
                .map(Book::getId)
                .collect(Collectors.toList());

        // 查询这些图书的主题
        List<BookTopic> bookTopics = bookTopicMapper.selectList(
                new LambdaQueryWrapper<BookTopic>().in(BookTopic::getBookId, bookIds)
        );

        // 统计每个主题的图书数量
        Map<String, Long> topicCountMap = bookTopics.stream()
                .collect(Collectors.groupingBy(BookTopic::getTopicName, Collectors.counting()));

        // 从数据库查询主题详细信息
        List<Topic> allTopics = topicMapper.selectList(
                new LambdaQueryWrapper<Topic>().eq(Topic::getStatus, 1)
        );

        // 创建主题名称到主题对象的映射
        Map<String, Topic> topicMap = allTopics.stream()
                .collect(Collectors.toMap(Topic::getName, topic -> topic, (existing, replacement) -> existing));

        // 转换为TopicVO并排序
        List<TopicVO> topics = topicCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    TopicVO vo = new TopicVO();
                    String topicName = entry.getKey();
                    vo.setName(topicName);
                    vo.setBookCount(entry.getValue());

                    // 从数据库获取主题信息
                    Topic topic = topicMap.get(topicName);
                    if (topic != null) {
                        vo.setDescription(topic.getDescription() != null ? topic.getDescription() : "探索" + topicName + "相关的精彩内容。");
                        vo.setIcon(topic.getIcon() != null ? topic.getIcon() : "fas fa-tag");
                        vo.setGradient(topic.getGradient() != null ? topic.getGradient() : "bg-gradient-to-br from-indigo-500 to-purple-600");
                    } else {
                        // 如果数据库中没有该主题，使用默认值
                        vo.setDescription("探索" + topicName + "相关的精彩内容。");
                        vo.setIcon("fas fa-tag");
                        // 使用hashCode生成默认渐变色
                        String[] defaultGradients = {
                            "bg-gradient-to-br from-indigo-500 to-purple-600",
                            "bg-gradient-to-br from-pink-500 to-rose-600",
                            "bg-gradient-to-br from-teal-500 to-green-600",
                            "bg-gradient-to-br from-blue-500 to-cyan-600"
                        };
                        vo.setGradient(defaultGradients[Math.abs(topicName.hashCode()) % defaultGradients.length]);
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        return topics;
    }

    @Override
    public List<RecommendBookVO> getLongTailRecommendations(int limit) {
        RecommendStrategyVO strategy = getRecommendStrategy();
        Integer threshold = strategy.getLongTailThreshold() != null ? strategy.getLongTailThreshold() : 10;

        // 黑名单：长尾推荐场景
        Set<Long> blacklistIds = loadBlacklistedBookIds("long-tail", null);
        // 冷启动配置：也要排除掉（防止同一本书在不同策略里被重复推荐）
        loadActiveColdStartConfigs().forEach(cs -> blacklistIds.add(cs.getBookId()));

        // 查询借阅次数小于阈值但评分较高的图书（确保 avgRating 不为 null）
        List<Book> longTailBooks = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .le(Book::getBorrowCount, threshold)
                        .isNotNull(Book::getAvgRating)
                        .gt(Book::getAvgRating, new BigDecimal("3.5"))
                        .notIn(!blacklistIds.isEmpty(), Book::getId, blacklistIds)
                        .orderByDesc(Book::getAvgRating)
                        .orderByAsc(Book::getBorrowCount)
                        .last("LIMIT " + limit)
        );

        // 如果有符合条件的冷门书，返回冷门推荐
        if (!longTailBooks.isEmpty()) {
            return longTailBooks.stream().map(book -> {
                RecommendBookVO vo = createRecommendVO(book);
                String ratingStr = book.getAvgRating() != null
                        ? String.format("%.1f", book.getAvgRating())
                        : "暂无";
                vo.setReason("冷门佳作（评分 " + ratingStr + "，借阅 " + book.getBorrowCount() + " 次）");
                return vo;
            }).collect(Collectors.toList());
        }

        // 如果没有符合条件的冷门书，返回随机推荐
        // 随机推荐同样需要应用黑名单
        List<Book> randomBooks = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .notIn(!blacklistIds.isEmpty(), Book::getId, blacklistIds)
                        .last("ORDER BY RAND() LIMIT " + limit)
        );

        return randomBooks.stream().map(book -> {
            RecommendBookVO vo = createRecommendVO(book);
            // 根据图书特征生成推荐理由
            String reason = generateRandomRecommendReason(book);
            vo.setReason(reason);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 为随机推荐的图书生成推荐理由
     */
    private String generateRandomRecommendReason(Book book) {
        List<String> reasons = new ArrayList<>();

        // 根据评分生成理由
        BigDecimal avgRating = book.getAvgRating();
        if (avgRating != null) {
            if (avgRating.compareTo(new BigDecimal("4.0")) >= 0) {
                reasons.add("高分推荐（评分 " + String.format("%.1f", avgRating) + "）");
            } else if (avgRating.compareTo(new BigDecimal("3.5")) >= 0) {
                reasons.add("优质图书（评分 " + String.format("%.1f", avgRating) + "）");
            }
        }

        // 根据借阅次数生成理由
        Integer borrowCount = book.getBorrowCount();
        if (borrowCount != null) {
            if (borrowCount > 50) {
                reasons.add("热门选择（借阅 " + borrowCount + " 次）");
            } else if (borrowCount > 10) {
                reasons.add("值得一读（借阅 " + borrowCount + " 次）");
            }
        }

        // 如果没有特定理由，使用通用推荐理由
        if (reasons.isEmpty()) {
            reasons.add("为您精心挑选");
        }

        return String.join("，", reasons);
    }

}

