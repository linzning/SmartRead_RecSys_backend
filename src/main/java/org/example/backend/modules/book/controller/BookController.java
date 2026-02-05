package org.example.backend.modules.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.common.ApiResponse;
import org.example.backend.common.PageResult;
import org.example.backend.common.util.UserContext;
import org.example.backend.modules.book.service.BookService;
import org.example.backend.vo.book.BookDetailVO;
import org.example.backend.vo.book.BookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 图书控制器
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "图书管理", description = "图书浏览、详情接口")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping("/list")
    @Operation(summary = "分页查询图书列表")
    public ApiResponse<PageResult<BookVO>> listBooks(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<BookVO> result = bookService.listBooks(page, size);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/filter")
    @Operation(summary = "多条件筛选查询图书", description = "支持关键字、主题、作者、出版社、评分筛选和排序")
    public ApiResponse<PageResult<BookVO>> filterBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false, defaultValue = "createTime") String sortBy,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<BookVO> result = bookService.filterBooks(
                keyword, topic, author, publisher, minRating, sortBy, status, page, size);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/detail/{bookId}")
    @Operation(summary = "获取图书详情")
    public ApiResponse<BookDetailVO> getBookDetail(@PathVariable Long bookId) {
        Long userId = UserContext.getCurrentUserId();
        BookDetailVO detail = bookService.getBookDetail(bookId, userId);
        return ApiResponse.success(detail);
    }
}

