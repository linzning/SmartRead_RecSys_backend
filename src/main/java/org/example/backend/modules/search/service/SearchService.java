package org.example.backend.modules.search.service;

import org.example.backend.common.PageResult;
import org.example.backend.vo.book.BookVO;

/**
 * 搜索服务接口
 */
public interface SearchService {
    
    /**
     * 搜索图书
     * @param query 搜索关键词
     * @param mode 搜索模式：keyword-关键词，semantic-语义，hybrid-混合
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    PageResult<BookVO> search(String query, String mode, Long page, Long size);
}

