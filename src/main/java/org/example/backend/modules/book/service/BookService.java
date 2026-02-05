package org.example.backend.modules.book.service;

import org.example.backend.common.PageResult;
import org.example.backend.vo.book.BookDetailVO;
import org.example.backend.vo.book.BookVO;

/**
 * 图书服务接口
 */
public interface BookService {
    
    /**
     * 分页查询图书列表
     */
    PageResult<BookVO> listBooks(Long page, Long size);
    
    /**
     * 多条件筛选查询图书列表
     * @param keyword 关键字（书名、作者、ISBN）
     * @param topic 主题/分类
     * @param author 作者
     * @param publisher 出版社
     * @param minRating 最低评分
     * @param sortBy 排序方式：createTime-最新, borrowCount-热门, avgRating-评分
     * @param status 状态：0-下架，1-上架，null-全部（管理员可用）
     * @param page 页码
     * @param size 每页大小
     */
    PageResult<BookVO> filterBooks(String keyword, String topic, String author, 
                                   String publisher, Double minRating, String sortBy,
                                   Integer status, Long page, Long size);
    
    /**
     * 获取图书详情
     */
    BookDetailVO getBookDetail(Long bookId, Long userId);
}

