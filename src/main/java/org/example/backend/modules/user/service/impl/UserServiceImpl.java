package org.example.backend.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.common.PageResult;
import org.example.backend.common.exception.BusinessException;
import org.example.backend.dto.user.*;
import org.example.backend.entity.*;
import org.example.backend.modules.book.repository.BookMapper;
import org.example.backend.modules.interaction.repository.BorrowRecordMapper;
import org.example.backend.modules.interaction.repository.CommentMapper;
import org.example.backend.modules.interaction.repository.FavoriteMapper;
import org.example.backend.modules.recommend.repository.UserPreferenceMapper;
import org.example.backend.modules.user.repository.*;
import org.example.backend.modules.user.service.UserService;
import org.example.backend.vo.user.BorrowHistoryVO;
import org.example.backend.vo.user.FavoriteVO;
import org.example.backend.vo.user.UserInfoVO;
import org.example.backend.vo.user.BooklistVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private BooklistMapper booklistMapper;
    
    @Autowired
    private BooklistBookMapper booklistBookMapper;
    
    @Autowired
    private UserPreferenceMapper userPreferenceMapper;
    
    @Autowired
    private UserInterestGuideMapper userInterestGuideMapper;
    
    @Autowired
    private BookViewMapper bookViewMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public UserInfoVO getCurrentUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        
        // 统计数据
        Long borrowCount = borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getUserId, userId)
        );
        Long favoriteCount = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>().eq(Favorite::getUserId, userId)
        );
        Long commentCount = commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>().eq(Comment::getUserId, userId)
        );
        
        vo.setBorrowCount(borrowCount);
        vo.setFavoriteCount(favoriteCount);
        vo.setCommentCount(commentCount);
        
        return vo;
    }
    
    @Override
    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().trim().isEmpty()) {
            if (updateDTO.getPassword().length() < 6) {
                throw new BusinessException("密码长度至少6位");
            }
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    public PageResult<BorrowHistoryVO> getBorrowHistory(Long userId, Long page, Long size) {
        Page<BorrowRecord> recordPage = new Page<>(page, size);
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId);
        // 先按申请时间排序，保证“待审核/待还书确认”也能出现在历史里
        wrapper.orderByDesc(BorrowRecord::getApplyTime);
        
        Page<BorrowRecord> result = borrowRecordMapper.selectPage(recordPage, wrapper);
        
        List<BorrowHistoryVO> historyList = result.getRecords().stream().map(record -> {
            BorrowHistoryVO vo = new BorrowHistoryVO();
            vo.setId(record.getId());
            vo.setBookId(record.getBookId());
            vo.setApplyTime(record.getApplyTime());
            vo.setBorrowTime(record.getBorrowTime());
            vo.setExpectedReturnTime(record.getExpectedReturnTime());
            vo.setReturnApplyTime(record.getReturnApplyTime());
            vo.setReturnTime(record.getReturnTime());
            vo.setStatus(record.getStatus());
            vo.setAuditStatus(record.getAuditStatus());
            vo.setOverdueDays(record.getOverdueDays());
            
            // 查询图书信息
            Book book = bookMapper.selectById(record.getBookId());
            if (book != null) {
                vo.setBookTitle(book.getTitle());
                vo.setBookAuthor(book.getAuthor());
                vo.setBookCoverUrl(book.getCoverUrl());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), historyList);
    }
    
    @Override
    public PageResult<FavoriteVO> getFavoriteList(Long userId, Long page, Long size) {
        Page<Favorite> favoritePage = new Page<>(page, size);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.orderByDesc(Favorite::getCreateTime);
        
        Page<Favorite> result = favoriteMapper.selectPage(favoritePage, wrapper);
        
        List<FavoriteVO> favoriteList = result.getRecords().stream().map(favorite -> {
            FavoriteVO vo = new FavoriteVO();
            vo.setId(favorite.getId());
            vo.setBookId(favorite.getBookId());
            vo.setCreateTime(favorite.getCreateTime());
            
            // 查询图书信息
            Book book = bookMapper.selectById(favorite.getBookId());
            if (book != null) {
                vo.setBookTitle(book.getTitle());
                vo.setBookAuthor(book.getAuthor());
                vo.setBookCoverUrl(book.getCoverUrl());
                vo.setBookAvgRating(book.getAvgRating());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), favoriteList);
    }
    
    // ========== 书单管理 ==========
    
    @Override
    @Transactional
    public Long createBooklist(Long userId, BooklistDTO booklistDTO) {
        Booklist booklist = new Booklist();
        booklist.setUserId(userId);
        booklist.setName(booklistDTO.getName());
        booklist.setDescription(booklistDTO.getDescription());
        booklist.setIsPublic(booklistDTO.getIsPublic() != null ? booklistDTO.getIsPublic() : 0);
        booklist.setBookCount(0);
        booklist.setCreateTime(LocalDateTime.now());
        booklist.setUpdateTime(LocalDateTime.now());
        
        booklistMapper.insert(booklist);
        return booklist.getId();
    }
    
    @Override
    @Transactional
    public void updateBooklist(Long userId, Long booklistId, BooklistDTO booklistDTO) {
        Booklist booklist = booklistMapper.selectById(booklistId);
        if (booklist == null) {
            throw new BusinessException("书单不存在");
        }
        
        if (!booklist.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此书单");
        }
        
        if (booklistDTO.getName() != null) {
            booklist.setName(booklistDTO.getName());
        }
        if (booklistDTO.getDescription() != null) {
            booklist.setDescription(booklistDTO.getDescription());
        }
        if (booklistDTO.getIsPublic() != null) {
            booklist.setIsPublic(booklistDTO.getIsPublic());
        }
        booklist.setUpdateTime(LocalDateTime.now());
        
        booklistMapper.updateById(booklist);
    }
    
    @Override
    @Transactional
    public void deleteBooklist(Long userId, Long booklistId) {
        Booklist booklist = booklistMapper.selectById(booklistId);
        if (booklist == null) {
            throw new BusinessException("书单不存在");
        }
        
        if (!booklist.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此书单");
        }
        
        booklistMapper.deleteById(booklistId);
    }
    
    @Override
    public PageResult<BooklistVO> getBooklistList(Long userId, Long page, Long size) {
        Page<Booklist> booklistPage = new Page<>(page, size);
        LambdaQueryWrapper<Booklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booklist::getUserId, userId);
        wrapper.orderByDesc(Booklist::getCreateTime);
        
        Page<Booklist> result = booklistMapper.selectPage(booklistPage, wrapper);
        
        List<BooklistVO> booklistList = result.getRecords().stream().map(booklist -> {
            BooklistVO vo = new BooklistVO();
            BeanUtils.copyProperties(booklist, vo);
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(page, size, result.getTotal(), booklistList);
    }
    
    @Override
    public BooklistVO getBooklistDetail(Long userId, Long booklistId) {
        Booklist booklist = booklistMapper.selectById(booklistId);
        if (booklist == null) {
            throw new BusinessException("书单不存在");
        }
        
        if (!booklist.getUserId().equals(userId) && booklist.getIsPublic() == 0) {
            throw new BusinessException("无权查看此书单");
        }
        
        BooklistVO vo = new BooklistVO();
        BeanUtils.copyProperties(booklist, vo);
        
        // 查询书单中的图书
        List<BooklistBook> booklistBooks = booklistBookMapper.selectList(
                new LambdaQueryWrapper<BooklistBook>().eq(BooklistBook::getBooklistId, booklistId)
        );
        
        List<BooklistVO.BookSimpleVO> books = booklistBooks.stream().map(bb -> {
            Book book = bookMapper.selectById(bb.getBookId());
            if (book != null) {
                BooklistVO.BookSimpleVO bookVO = new BooklistVO.BookSimpleVO();
                bookVO.setId(book.getId());
                bookVO.setTitle(book.getTitle());
                bookVO.setAuthor(book.getAuthor());
                bookVO.setCoverUrl(book.getCoverUrl());
                bookVO.setAvgRating(book.getAvgRating() != null ? book.getAvgRating().doubleValue() : 0.0);
                return bookVO;
            }
            return null;
        }).filter(b -> b != null).collect(Collectors.toList());
        
        vo.setBooks(books);
        return vo;
    }
    
    @Override
    @Transactional
    public void addBookToBooklist(Long userId, Long booklistId, Long bookId) {
        Booklist booklist = booklistMapper.selectById(booklistId);
        if (booklist == null) {
            throw new BusinessException("书单不存在");
        }
        
        if (!booklist.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此书单");
        }
        
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        // 检查是否已存在
        BooklistBook exist = booklistBookMapper.selectOne(
                new LambdaQueryWrapper<BooklistBook>()
                        .eq(BooklistBook::getBooklistId, booklistId)
                        .eq(BooklistBook::getBookId, bookId)
        );
        
        if (exist != null) {
            throw new BusinessException("图书已在此书单中");
        }
        
        BooklistBook booklistBook = new BooklistBook();
        booklistBook.setBooklistId(booklistId);
        booklistBook.setBookId(bookId);
        booklistBook.setAddTime(LocalDateTime.now());
        booklistBookMapper.insert(booklistBook);
        
        // 更新书单图书数量
        booklist.setBookCount(booklist.getBookCount() + 1);
        booklistMapper.updateById(booklist);
    }
    
    @Override
    @Transactional
    public void removeBookFromBooklist(Long userId, Long booklistId, Long bookId) {
        Booklist booklist = booklistMapper.selectById(booklistId);
        if (booklist == null) {
            throw new BusinessException("书单不存在");
        }
        
        if (!booklist.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此书单");
        }
        
        BooklistBook booklistBook = booklistBookMapper.selectOne(
                new LambdaQueryWrapper<BooklistBook>()
                        .eq(BooklistBook::getBooklistId, booklistId)
                        .eq(BooklistBook::getBookId, bookId)
        );
        
        if (booklistBook == null) {
            throw new BusinessException("图书不在此书单中");
        }
        
        booklistBookMapper.deleteById(booklistBook.getId());
        
        // 更新书单图书数量
        booklist.setBookCount(Math.max(0, booklist.getBookCount() - 1));
        booklistMapper.updateById(booklist);
    }
    
    // ========== 兴趣偏好管理 ==========
    
    @Override
    @Transactional
    public void setPreference(Long userId, PreferenceDTO preferenceDTO) {
        // 删除该类型的旧偏好
        userPreferenceMapper.delete(
                new LambdaQueryWrapper<UserPreference>()
                        .eq(UserPreference::getUserId, userId)
                        .eq(UserPreference::getPreferenceType, preferenceDTO.getPreferenceType())
        );
        
        // 添加新偏好
        if (preferenceDTO.getPreferenceValues() != null && !preferenceDTO.getPreferenceValues().isEmpty()) {
            double weight = 1.0 / preferenceDTO.getPreferenceValues().size();
            for (String value : preferenceDTO.getPreferenceValues()) {
                UserPreference preference = new UserPreference();
                preference.setUserId(userId);
                preference.setPreferenceType(preferenceDTO.getPreferenceType());
                preference.setPreferenceValue(value);
                preference.setWeight(weight);
                preference.setUpdateTime(LocalDateTime.now());
                userPreferenceMapper.insert(preference);
            }
        }
    }
    
    @Override
    public List<UserPreference> getPreferences(Long userId) {
        return userPreferenceMapper.selectList(
                new LambdaQueryWrapper<UserPreference>().eq(UserPreference::getUserId, userId)
        );
    }
    
    @Override
    @Transactional
    public void completeInterestGuide(Long userId, InterestGuideDTO guideDTO) {
        UserInterestGuide guide = userInterestGuideMapper.selectOne(
                new LambdaQueryWrapper<UserInterestGuide>().eq(UserInterestGuide::getUserId, userId)
        );
        
        if (guide == null) {
            guide = new UserInterestGuide();
            guide.setUserId(userId);
            guide.setCreateTime(LocalDateTime.now());
        }
        
        try {
            if (guideDTO.getSelectedTopics() != null) {
                guide.setSelectedTopics(objectMapper.writeValueAsString(guideDTO.getSelectedTopics()));
            }
            if (guideDTO.getSelectedAuthors() != null) {
                guide.setSelectedAuthors(objectMapper.writeValueAsString(guideDTO.getSelectedAuthors()));
            }
        } catch (Exception e) {
            throw new BusinessException("数据格式错误");
        }
        
        guide.setIsCompleted(1);
        guide.setUpdateTime(LocalDateTime.now());
        
        if (guide.getId() == null) {
            userInterestGuideMapper.insert(guide);
        } else {
            userInterestGuideMapper.updateById(guide);
        }
        
        // 保存兴趣偏好
        if (guideDTO.getSelectedTopics() != null && !guideDTO.getSelectedTopics().isEmpty()) {
            PreferenceDTO preferenceDTO = new PreferenceDTO();
            preferenceDTO.setPreferenceType("topic");
            preferenceDTO.setPreferenceValues(guideDTO.getSelectedTopics());
            setPreference(userId, preferenceDTO);
        }
    }
    
    @Override
    public boolean isInterestGuideCompleted(Long userId) {
        UserInterestGuide guide = userInterestGuideMapper.selectOne(
                new LambdaQueryWrapper<UserInterestGuide>().eq(UserInterestGuide::getUserId, userId)
        );
        return guide != null && guide.getIsCompleted() != null && guide.getIsCompleted() == 1;
    }
    
    // ========== 浏览记录 ==========
    
    @Override
    @Transactional
    public void recordBookView(Long userId, Long bookId) {
        BookView bookView = new BookView();
        bookView.setUserId(userId);
        bookView.setBookId(bookId);
        bookView.setViewTime(LocalDateTime.now());
        bookView.setDuration(0); // 默认0，前端可以后续更新
        bookViewMapper.insert(bookView);
    }
}


