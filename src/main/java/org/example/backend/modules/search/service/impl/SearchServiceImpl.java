package org.example.backend.modules.search.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.common.PageResult;
import org.example.backend.common.constants.Constants;
import org.example.backend.entity.Book;
import org.example.backend.entity.BookTopic;
import org.example.backend.modules.book.repository.BookMapper;
import org.example.backend.modules.book.repository.BookTopicMapper;
import org.example.backend.modules.search.service.SearchService;
import org.example.backend.vo.book.BookVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索服务实现
 */
@Service
public class SearchServiceImpl implements SearchService {
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private BookTopicMapper bookTopicMapper;
    
    @Override
    public PageResult<BookVO> search(String query, String mode, Long page, Long size) {
        if (query == null || query.trim().isEmpty()) {
            return PageResult.of(page, size, 0L, Collections.emptyList());
        }
        
        Page<Book> bookPage = new Page<>(page, size);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, 1); // 只查询上架的图书
        
        // 根据模式选择搜索方式
        if (Constants.SEARCH_MODE_KEYWORD.equals(mode) || Constants.SEARCH_MODE_HYBRID.equals(mode)) {
            // 关键词搜索：书名、作者、ISBN
            wrapper.and(w -> w.like(Book::getTitle, query)
                    .or().like(Book::getAuthor, query)
                    .or().like(Book::getIsbn, query));
        }
        
        // 语义搜索（占位实现，未来可接入向量库/ES）
        if (Constants.SEARCH_MODE_SEMANTIC.equals(mode) || Constants.SEARCH_MODE_HYBRID.equals(mode)) {
            // TODO: 接入语义搜索（向量库/ES）
            // 当前使用关键词搜索作为占位
            if (!Constants.SEARCH_MODE_HYBRID.equals(mode)) {
                wrapper.and(w -> w.like(Book::getTitle, query)
                        .or().like(Book::getAuthor, query)
                        .or().like(Book::getSummary, query));
            }
        }
        
        wrapper.orderByDesc(Book::getBorrowCount); // 按借阅次数排序
        
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
}

