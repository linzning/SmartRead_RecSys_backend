package org.example.backend.modules.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.common.ApiResponse;
import org.example.backend.common.PageResult;
import org.example.backend.common.constants.Constants;
import org.example.backend.modules.search.service.SearchService;
import org.example.backend.vo.book.BookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "图书搜索", description = "关键词、语义、混合搜索接口")
public class SearchController {
    
    @Autowired
    private SearchService searchService;
    
    @GetMapping("/search")
    @Operation(summary = "搜索图书", description = "支持关键词、语义、混合三种搜索模式")
    public ApiResponse<PageResult<BookVO>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = Constants.SEARCH_MODE_KEYWORD) String mode,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<BookVO> result = searchService.search(q, mode, page, size);
        return ApiResponse.success(result);
    }
}

